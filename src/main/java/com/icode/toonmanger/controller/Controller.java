package com.icode.toonmanger.controller;


import com.icode.toonmanger.config.*;
import com.icode.toonmanger.mapper.FaMapper;

import com.icode.toonmanger.mapper.MapperForTaskManager;
import com.icode.toonmanger.mapper.MapperForWorker;
import com.icode.toonmanger.security.User;
import io.netty.util.concurrent.FastThreadLocalThread;
import org.apache.ibatis.annotations.Select;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Controller {
    @Value("${server.uploadroot}")
    String uploadroot ;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    protected RedisTemplate<String,Object> redis;

    @Autowired
    protected MapperForTaskManager tmMapper;

    @Autowired
    protected MapperForWorker workerMapper;

    @Autowired
    protected FaMapper selectMapper;


    @RequestMapping(value = "/uploadFile.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public Mono<CResult> uploadFile(@RequestPart("file")Mono<FilePart> monoFile){
        CResult ret = new CResult();
        String uuid = UUID.randomUUID().toString().replace("-","");
        final String[] UPLOADABLE_FILE_EXTENSION = { "png", "jpg", "clip","cmc", "psd", "zip", "tar", "sut", "cls" };



        return monoFile.map( fp -> {
            String fname = fp.filename();

            String ext;
            String uploaddir = uploadroot;
            String filename ="";

            try {

                ext = fname.substring(fname.lastIndexOf(".") + 1);
                String yyyymmdd = sdf.format(new Date());
                String yyyymm = yyyymmdd.substring(0,6);
                String dd = yyyymmdd.substring(6);


                filename = "/"+yyyymm+"/"+dd+"/";
                uploaddir = uploadroot+filename;
                Path path =  Paths.get(uploaddir);
                Files.createDirectories(path);

            }catch(Exception e){
                ret.setStatus("ERROR");
                return ret;
            }
            ext = ext.trim().toLowerCase();


            if(!Arrays.stream(UPLOADABLE_FILE_EXTENSION).anyMatch(ext::equals)){
                ret.setStatus("ERROR");
                return ret;
            }

            filename = filename+uuid+"."+ext;


            fp.transferTo(Paths.get(uploadroot+ filename)).subscribe();
            ret.put("filename", filename);
            return ret;

        });

    }


    @RequestMapping(value = "/uploadFileEntry.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public Mono<CResult> uploadFileEntry(@RequestPart("file")Mono<FilePart> monoFile,
                                           @RequestPart("uploadtoken") String uploadtoken,
                                           @RequestPart("t") String t,
                                           @RequestPart("kind") String kind){

        CResult ret = new CResult();
        CParam param = new CParam(redis);
        String uuid = UUID.randomUUID().toString().replace("-","");
        final String[] UPLOADABLE_FILE_EXTENSION = { "png", "jpg", "clip","cmc", "psd", "zip", "tar", "sut", "cls", "txt","skp", "pdf"};
        param.put("t", t);
        User user = param.getUser();

        if(user == null){
            throw new RuntimeException("invalidToken");
        }

        return monoFile.map( fp -> {
            String fname = fp.filename();
            CMap entryVersion = new CMap();
            CMap entryUploadCheck = null;

            int eResult = 0;

            String ext;
            String uploaddir = uploadroot;
            String filename ="";

            try {

                ext = fname.substring(fname.lastIndexOf(".") + 1);
                String yyyymmdd = sdf.format(new Date());
                String yyyymm = yyyymmdd.substring(0,6);
                String dd = yyyymmdd.substring(6);


                filename = "/"+yyyymm+"/"+dd+"/";
                uploaddir = uploadroot+filename;
                Path path =  Paths.get(uploaddir);
                Files.createDirectories(path);

            }catch(Exception e){
                ret.setStatus("ERROR");
                return ret;
            }
            ext = ext.trim().toLowerCase();


            if(!Arrays.stream(UPLOADABLE_FILE_EXTENSION).anyMatch(ext::equals)){
                ret.setStatus("ERROR");
                return ret;
            }

            filename = filename+uuid+"."+ext;

            fp.transferTo(Paths.get(uploadroot+ filename)).subscribe();

            File checkFile = new File(uploadroot + filename);

            if(!checkFile.exists()){
                ret.setStatus("ERROR-check");
                return ret;
            }


            param.put("src", filename);
            param.put("uploadtoken", uploadtoken);
            param.put("taskmanagerpkey", user.getTaskManagerPkey());
            param.put("workerpkey" , user.getWorkerPkey());


            if("tm".equals(kind)){
                entryVersion = selectMapper.getTmEntryVersionPkey(param);
                param.put("entryversionpkey", entryVersion.getS("entryversionpkey"));
                eResult =  tmMapper.setEntryVersionSrc(param);
                entryUploadCheck = selectMapper.getEntryVersionUploadCheck(entryVersion);

            }else if("me".equals(kind)){
                entryVersion = selectMapper.getMeEntryVersionPkey(param);
                param.put("entryversionpkey", entryVersion.getS("entryversionpkey"));
                eResult = workerMapper.setEntryVersionSrc(param);
                entryUploadCheck = selectMapper.getEntryVersionUploadCheck(entryVersion);
            }


            if(eResult != 1 || entryUploadCheck == null || !"use".equals(entryUploadCheck.getS("token"))){
                throw  new RuntimeException("entryUploadError");
            }else{
                ret.put("result", "ok");
            }

            return ret;

        });
    }


    @RequestMapping(value = "/uploadFileTaskEntry.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public Mono<CResult> uploadFileTaskEntry(@RequestPart("file")Mono<FilePart> monoFile,
                                         @RequestPart("uploadtoken") String uploadtoken,
                                         @RequestPart("t") String t
                                         ){

        CResult ret = new CResult();
        CParam param = new CParam(redis);
        String uuid = UUID.randomUUID().toString().replace("-","");
        final String[] UPLOADABLE_FILE_EXTENSION = { "png", "jpg", "clip","cmc", "psd", "zip", "tar", "sut", "cls", "txt", "pdf", "skp" };
        param.put("t", t);
        User user = param.getUser();

        if(user == null){
            throw new RuntimeException("invalidToken");
        }

        return monoFile.map( fp -> {
            String fname = fp.filename();
            CMap entryVersion = new CMap();
            CMap entryUploadCheck = null;

            int eResult = 0;

            String ext;
            String uploaddir = uploadroot;
            String filename ="";

            try {

                ext = fname.substring(fname.lastIndexOf(".") + 1);
                String yyyymmdd = sdf.format(new Date());
                String yyyymm = yyyymmdd.substring(0,6);
                String dd = yyyymmdd.substring(6);


                filename = "/"+yyyymm+"/"+dd+"/";
                uploaddir = uploadroot+filename;
                Path path =  Paths.get(uploaddir);
                Files.createDirectories(path);

            }catch(Exception e){
                ret.setStatus("ERROR");
                return ret;
            }
            ext = ext.trim().toLowerCase();


            if(!Arrays.stream(UPLOADABLE_FILE_EXTENSION).anyMatch(ext::equals)){
                ret.setStatus("ERROR");
                return ret;
            }

            filename = filename+uuid+"."+ext;

            fp.transferTo(Paths.get(uploadroot+ filename)).subscribe();

            File checkFile = new File(uploadroot + filename);

            if(!checkFile.exists()){
                ret.setStatus("ERROR-EMPTY");
                return ret;
            }



            param.put("uploadtoken", uploadtoken);
            param.put("taskmanagerpkey", user.getTaskManagerPkey());


            CMap taskEntry = selectMapper.getTaskEntryPkey(param);
            taskEntry.put("src", filename);
            int tResult = tmMapper.setTaskEntrySrc(taskEntry);
            CMap taskEntryUploadCheck = selectMapper.getTaskEntryUploadCheck(taskEntry);


            if(tResult != 1 || taskEntryUploadCheck == null|| !"use".equals(taskEntryUploadCheck.getS("token")) ){
                throw  new RuntimeException("taskEntryUploadError");
            }else{
                ret.put("result", "ok");
            }

            return ret;

        });
    }




    @GetMapping(value = "/file/{*fileName}")
    @ResponseBody
    public Mono<Void> downloadFile(@PathVariable String fileName, ServerHttpResponse response) {

        ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;

        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);

        File file = Paths.get(uploadroot+ fileName).toFile();

        response.getHeaders().setContentLength(file.length());

        return zeroCopyResponse.writeWith(file, 0, file.length());
    }




    @RequestMapping(value = "/file/downloadFile.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public Mono<Void> downloadFile(@GetParam CParam param,  ServerHttpResponse response){

        ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;

        param.validateEmpty("entryversionpkey");

        CMap entryVersion = selectMapper.getEntryVersion(param);


        String fileName = entryVersion.getS("name");
        String fileSrc = entryVersion.getS("src");

        String fileNameEncoding =  fileNameEncoder(fileName);

        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + fileNameEncoding);
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);

        File file = Paths.get(uploadroot + fileSrc).toFile();

        response.getHeaders().setContentLength(file.length());

        return zeroCopyResponse.writeWith(file, 0, file.length());

    }

    @RequestMapping(value = "/file/downloadDirectory.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public Flux<DataBuffer> downloadDirectory(@GetParam CParam param, ServerHttpResponse response){


        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.getHeaders().setContentDisposition(ContentDisposition.attachment().filename("a.zip").build());
        Flux<DataBuffer> ret = null;

        param.validateEmpty("entrypkey", "workpkey", "updatesetpkey");

        CMap directory = selectMapper.getEntryDirectory(param);

        List<CMap> folderList = new ArrayList<>();

        List<CMap> fileList = selectMapper.listUpdateSetEntryFile(param);

        folderWrap(param, folderList);

        for(CMap folder : folderList){
            List<CMap> entryFileList = selectMapper.listUpdateSetEntryFile(folder);
            for(CMap file : entryFileList){
                fileList.add(file);
            }
        }

        for (CMap file : fileList){
            folderList.add(file);
        }


        String fileNameEncoding = fileNameEncoder(directory.getS("name"));

        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + fileNameEncoding + ".zip");
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);


        PipedInputStream pis = new PipedInputStream();

        Thread zipThread = zipThread(pis, folderList, "WORK");

        zipThread.start();


        ret = DataBufferUtils.readInputStream(() ->{     return pis;    },response.bufferFactory(),8196);


        return ret;

    }


    @RequestMapping(value = "/file/downloadUpdateSet.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public  Flux<DataBuffer> downloadUpdateSet(@GetParam CParam param, ServerHttpResponse response){

        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.getHeaders().setContentDisposition(ContentDisposition.attachment().filename("a.zip").build());
        Flux<DataBuffer> ret = null;


        param.validateEmpty("updatesetpkey");

        List<CMap> entryList = new ArrayList<>();

        CMap updateSet = selectMapper.getUpdateSet(param);

        param.put("workpkey", updateSet.getS("workpkey"));

        List<CMap> listUpdateSetEntry = selectMapper.listUpdateSetEntry(param);

        String fileNameEncoding = fileNameEncoder(updateSet.getS("zipname"));

        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + fileNameEncoding + ".zip");
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);


        PipedInputStream pis = new PipedInputStream();

        Thread zip = zipThread(pis,listUpdateSetEntry, "WORK");


        zip.start();


        ret = DataBufferUtils.readInputStream(() ->{     return pis;    },response.bufferFactory(),8196);


        return  ret;

    }





    @RequestMapping(value = "/file/downloadTaskEntryAll.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public  Flux<DataBuffer> downloadTaskDoneEntry(@GetParam CParam param, ServerHttpResponse response){

        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.getHeaders().setContentDisposition(ContentDisposition.attachment().filename("a.zip").build());
        Flux<DataBuffer> ret = null;


        param.validateEmpty("taskpkey");

        List<CMap> taskEntryList = selectMapper.listTaskEntry(param);

        CMap zipName =selectMapper.getTaskEntryAllZipName(param);


        String fileNameEncoding = fileNameEncoder(zipName.getS("zipname"));

        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + fileNameEncoding + ".zip");
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);


        PipedInputStream pis = new PipedInputStream();

        Thread zip = zipThread(pis,taskEntryList, "TASK");


        zip.start();


        ret = DataBufferUtils.readInputStream(() ->{     return pis;    },response.bufferFactory(),8196);


        return  ret;
    }


    @RequestMapping(value = "/file/downloadTaskEntryDirectory.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public  Flux<DataBuffer> downloadTaskEntryDirectory(@GetParam CParam param, ServerHttpResponse response){

        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.getHeaders().setContentDisposition(ContentDisposition.attachment().filename("a.zip").build());
        Flux<DataBuffer> ret = null;


        param.validateEmpty("taskentrypkey");

        CMap taskEntryFolder = selectMapper.getEntryCDirectory(param);
        List<CMap> taskEntryList = new ArrayList<>();
        taskEntryList =  taskWrap(taskEntryFolder, taskEntryList);


        CMap zipName =selectMapper.getTaskEntryFolderZipName(param);


        String fileNameEncoding = fileNameEncoder(zipName.getS("zipname"));

        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + fileNameEncoding + ".zip");
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);


        PipedInputStream pis = new PipedInputStream();

        Thread zip = zipThread(pis,taskEntryList, "TASK");

        zip.start();

        ret = DataBufferUtils.readInputStream(() ->{     return pis;    },response.bufferFactory(),8196);


        return  ret;
    }


    @RequestMapping(value = "/file/downloadTaskEntryFile.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public  Mono<Void> downloadTaskEntryFile(@GetParam CParam param, ServerHttpResponse response){

        ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;

        param.validateEmpty("taskentrypkey");

        CMap getTaskEntryFile = selectMapper.getTaskEntryFile(param);


        String fileName = getTaskEntryFile.getS("name");
        String fileSrc = getTaskEntryFile.getS("src");

        String fileNameEncoding =  fileNameEncoder(fileName);

        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment;  filename=" + fileNameEncoding);
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);

        File file = Paths.get(uploadroot + fileSrc).toFile();

        response.getHeaders().setContentLength(file.length());

        return zeroCopyResponse.writeWith(file, 0, file.length());

    }



    @RequestMapping(value = "/checkToken.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult checkToken(@PostParam CParam param){

        CResult ret = new CResult();

        HashOperations<String, String, String> hash = redis.opsForHash();
        String status = hash.get(getRedisKey(param.getS("t")),"status");


        if(status == null){
            ret.put("status","NOTLOGIN");
        }

        ret.setStatus("OK");

        return ret;
    }

    @RequestMapping(value = "/getUser.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult getUser(@PostParam CParam param){

        CResult ret = new CResult();

        HashOperations<String, String, String> hash = redis.opsForHash();
        String name = hash.get(getRedisKey(param.getS("t")),"name");

        if(name == null){
            ret.put("islogin","N");
        }else{
            ret.put("name",name);
            ret.put("islogin","Y");
        }

        ret.setStatus("OK");
        return ret;
    }



    @RequestMapping(value = "/logout.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult logOut(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("t");

        redis.delete(getRedisKey(param.getS("t")));

        ret.setStatus("OK");

        return ret;
    }





    public String fileNameEncoder (String filename){
        return URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
    }


    public void folderWrap (CMap param, List<CMap> folderList){

        List<CMap> list = selectMapper.listlistUpadteSetRelForder(param);

        for( CMap entry : list){
            folderList.add(entry);
            folderWrap(entry, folderList);
        }

    }


    public List<CMap> taskWrap (CMap param,List<CMap> entrylist){

        List<CMap> list = selectMapper.listTaskEntryForRel(param);

        for( CMap entry : list){
            if ("N".equals(entry.getS("isfile"))){
                entrylist.add(entry);
                taskWrap(entry, entrylist);
            }else {
                entrylist.add(entry);
            }
        }

        return  entrylist;

    }

    public Thread zipThread(PipedInputStream pis, List<CMap> entryList, String kind){
        Thread a = new FastThreadLocalThread(){
            @Override
            public void run() {
                try {
                    PipedOutputStream pos = new PipedOutputStream();
                    pis.connect(pos);
                    ZipOutputStream zos = new ZipOutputStream(pos);
                    zos.setLevel(0);
                    CMap tree = new CMap();
                    String uuid = UUID.randomUUID().toString();
                    for (CMap map : entryList) {

                        String rel = map.getS("rel", "-1");
                        String isfile = map.getS("isfile", "Y");
                        String name = "";

                        if("TASK".equals(kind)){
                            name = map.getS("name", "NONAME" + map.getS("taskentrypkey"));
                        }
                        if("WORK".equals((kind))){
                            name = map.getS("name", "NONAME" + map.getS("entrypkey"));
                        }

                        CMap cur = tree;
                        if ("N".equals(isfile)) {
                            String path = tree.getS(rel, "");
                            if("TASK".equals(kind)){
                                tree.put(map.getS("taskentrypkey"), path + name + "/");
                            }
                            if("WORK".equals((kind))){
                                tree.put(map.getS("entrypkey"), path + name + "/");
                            }
                        }

                    }

                    for(CMap entry: entryList){
                        try {

                            if ("Y".equals(entry.getS("isfile"))) {

                                File file = Paths.get(uploadroot + entry.getS("src")).toFile();
                                String fileName = entry.getS("name");
                                zos.putNextEntry(new ZipEntry(tree.getS(entry.getS("rel"), "") + fileName));
                                int len;
                                FileInputStream in = null;
                                byte[] buf = new byte[1024];
                                try{
                                    in = new FileInputStream(file);
                                    while ((len = in.read(buf)) > 0) {
                                        zos.write(buf, 0, len);
                                    }
                                }catch (IOException e){

                                }finally {
                                    if( in != null) in.close();
                                }

                                zos.closeEntry();
                            }

                            if ("N".equals(entry.getS("isfile"))) {
                                if("TASK".equals(kind)){
                                    zos.putNextEntry(new ZipEntry(tree.getS(entry.getS("taskentrypkey"), "")));
                                }
                                if("WORK".equals(kind)){
                                    zos.putNextEntry(new ZipEntry(tree.getS(entry.getS("entrypkey"), "")));
                                }
                                zos.closeEntry();
                            }

                            zos.flush();

                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    zos.finish();
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        return  a;
    }


    protected CResult requestToCMap(HttpServerRequest request){
        CResult cr = new CResult();
        return cr;
    }

    public void throwError(String msg){
        throw new RuntimeException(msg);
    }
    public CResult errorMap(String msg) {
        CResult ret = new CResult();
        ret.setStatus("error");
        ret.setMsg(msg);

        return ret;
    }

    private String getRedisKey(String token){
        String rkey = "ICODE:SESSION:" + token;

        return rkey;
    }

    protected String makeSession(String kind,CMap userinfo){
        String token = UUID.randomUUID().toString();
        String rkey = getRedisKey(token);

        try {
            HashOperations<String, String, String> hash = redis.opsForHash();

            Set sss = userinfo.keySet();

            for (Object ikey : sss) {
                String key = ikey.toString();
                hash.put(rkey, key, userinfo.getS(key));
            }

            hash.put(rkey, "kind",kind);

            redis.expire(rkey,Duration.ofDays(1));

            return token;
        }catch(Exception e){
            throw new RuntimeException("NOTLOGIN");
        }
    }

}
