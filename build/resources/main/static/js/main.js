var currentData = {};
var currentUser = null;

function init() {   
    console.log('init');              
    currentUser = {t : getStorageToken($('#app').attr('user') + '.t')};
    if(!currentUser.t) { location.href = 'login.html'; return;}    
    
    loadFragments().then(res => {
            setCurrentUser($('#app').attr('user'));
            runSetup()
        }
    );    
}

function getAPI(role) {
    switch (role) {
        case "com" : { return COMAPI; }
        case "tm" : { return TMAPI; }
        case "me" : { return MEAPI; }
    }    
}

function filterRole(selector) {
    const roleElements = document.querySelectorAll(selector + ' [role]');
    roleElements.forEach(ele => {        
        const rolelist = ele.getAttribute('role').split(', ');
        if(!rolelist.includes(currentUser.auth)) { $(ele).hide(); return;}
        $(ele).show();
    });
}

function setCurrentUser(role){    
    const API = getAPI(role);
    API.getUser(currentUser).then(res => {
        if(res.body.islogin != "Y") {
            alert("세션이 만료되었거나 로그인이 필요합니다.");
            logout(role);
            return ;
        }
        Object.assign(currentUser, res.body);
        currentUser.auth = role;            
        bindView($('nav')[0], currentUser);
        bindView($('header')[0], currentUser);            
    });
}

function login(key, name){
    sessionStorage.setItem(key + '.t', name);    
    location.href="overview.html";
}

function logout(role) {
    const API = getAPI(role);    
    API.userLogout().finally(res => {
        currentUser = null;
        sessionStorage.removeItem(role + '.t');
        location.href = "login.html";
    });
}

function findItems(element) {
    const items = element.querySelectorAll('item');
    if (items.length == 0) return;

    if (window.listtemplates == undefined) {
        window.listtemplates = [];
    }

    for (let i = 0; i < items.length; i++) {
        let item = items[i];
        let innerItems = item.querySelectorAll("item");
        if (innerItems.length <= 0) {
            const parent = item.parentNode;
            const itemview = parent.removeChild(item);
            window.listtemplates.push(itemview);
            parent.tempdom = itemview;            
            parent.setAttribute("templateid", window.listtemplates.length -1);
        }     
    }
    findItems(element);
}

function clearItems(selector) {
    let item = selector ? selector + ' item' : "item";
    let empty = selector ? selector + ' emptyitem' : "emptyitem";
    let listcon = selector ? selector + ' [listidx]' : undefined ;
    if(item) {$(item).remove()};
    if(empty) {$(empty).hide()};
    if(listcon) {
        $(listcon).attr('listidx', "0");        
    }
    $(selector + " .bindmore").show();
}

function clearOptions(selector) {
    let item = selector == undefined ?  "option[c-item]" : selector + ' option[c-item]' ;    
    if(item) {$(item).remove()};    
}

function bindView(view, data) {
    
    const customBinds = view.querySelectorAll('[c-bind]');
    if (customBinds.length <= 0) {
        return
    }

    customBinds.forEach(bind => {        
        if(bind.getAttribute('binddone') == "Y") { return; }
        bind.binddata = data;
        const bindingValueArray = bind.getAttribute('c-bind').split(",")
        bindingValueArray.forEach(value => {
            const valueArray = value.split(":")
            const commandKey = valueArray[0]
            let commandValue = "";

            if (valueArray.length < 2) return;
            try {
                commandValue = eval(`data.${commandKey}`);
            } catch (e) {                
            }
            let commandType = "nomap";
            try { commandType = valueArray[1]; } catch (e) { }
            const fnName = valueArray[2];
            try {
                const fn = eval(fnName);
                commandValue = fn(commandValue, data, bind);
            } catch (e) {

             }

            if(commandValue == undefined) return;
            switch (commandType) {
                case 'text': {
                    bind.innerText = commandValue;                    
                    break;
                }
                case 'option': {                    
                    bind.text = commandValue;                    
                    break;
                }
                case 'select': {                    
                    const option = Array.from(bind.options).find(opt => { return opt.value == commandValue });                    
                    bind.selectedIndex = option.index;
                    break;
                }
                case 'listtemplete': {
                    if(bind instanceof HTMLSelectElement) {
                        let tempitem = bind.querySelector('[listtempitem]');
                        if(tempitem != undefined) {
                            tempitem.removeAttribute('listtempitem');
                            tempitem.setAttribute('c-bind', tempitem.getAttribute(bind.getAttribute("c-tag")));
                            bind.childTemp = tempitem;
                            tempitem.remove();
                        }
                        
                        let oldval = bind.querySelectorAll('[c-bind]');
                        oldval.forEach(ele => {ele.remove();});

                        let container = document.createElement('item');
                        let cloneTemp = bind.childTemp.cloneNode(true);
                        container.append(cloneTemp);
                        for (let i = 0; i < commandValue.length; i++) {
                            try {
                                const clone = container.cloneNode(true);
                                commandValue[i].idx = i;                                                            
                                bindView(clone, commandValue[i]);                                
                                bind.appendChild(clone.querySelector('[c-bind]'));
                            } catch (E) {
    
                            }
                        }                            
                    }
                    break;
                }
                case 'list': {
                    if (commandValue === undefined) { return }
                    if (commandValue.length == 0) {
                        let empty = bind.querySelector('emptyitem');
                        let bindmore = bind.querySelector('.bindmore');
                        if(bindmore) { bindmore.style.display = "none"};                        
                        if(empty == undefined) { break; }
                        empty.style.display = "block";
                        break;
                    }
                    var childTemp = bind.itemTemp;
                        if (bind.itemTemp == undefined) {
                            var oo = bind.getAttribute("templateid");                            
                            childTemp = window.listtemplates ? window.listtemplates[oo] : childTemp;
                            childTemp = bind.tempdom ? bind.tempdom : childTemp;
                        }
                        
                        for (let i = 0; i < commandValue.length;  i++) {                                                        
                            try {
                                const clone = childTemp.cloneNode(true);                                                                
                                bindView(clone, commandValue[i]);
                                if(bind.querySelector('.bindmore')) {
                                    bind.insertBefore(clone, bind.querySelector('.bindmore'));
                                    bind.setAttribute('listidx', parseInt(bind.getAttribute('listidx')) + 1);                                    
                                } else {                                    
                                    bind.appendChild(clone);
                                }                                
                            } catch (E) {          
                                                     
                            }
                        }
                    break;
                    
                }
                case 'nomap': {
                    break;
                }
                case 'radio': {
                    if(bind.value == commandValue) { bind.checked = true }                    
                    break;
                }
                default: {
                    const attrKey = valueArray[1];
                    if (attrKey) bind.setAttribute(attrKey, commandValue);                
                    break;
                }
            }
            if(bind.getAttribute('bindonce') == "Y") { bind.setAttribute('binddone', "Y");}                    
        })
    });

}

function setBindMore(next, bindid, msg) {
    if(next == "Y") {
        $('.bindmore[bind-id="' + bindid +'"]').show();
    }        
    if(next == "N") {
        if(msg) { showToast(msg) };
        $('.bindmore[bind-id="' + bindid +'"]').hide();
    }
}

function getListStart(id) {
    const listEle = document.getElementById(id);
    return listEle.getAttribute('listidx');
}

function getListLimit(id) {
    const listEle = document.getElementById(id);
    return listEle.getAttribute('step');
}

function getStorageToken(key) {
    const token = sessionStorage.getItem(key);    
    return token;
}

function loadFragments() {
    const promises = [];
        $("[loadsrc]").each(function(){
            const promise = new Promise((resolve, reject) => {
                const link = $(this).attr("loadsrc");
                $(this).load(link, resolve());              
            });                      
            promises.push(promise);
        });
    return Promise.all(promises);
}

function getDataFrom(selector, ele) {
    const ret = {};    
    if(!ele) { ele = document }    
    $(selector + " [name]", ele).each(function() {        
        if($(this).get(0) instanceof HTMLInputElement) {
            if($(this).attr('type') == "radio") {
                if($(this).is(":checked")) {
                    ret[$(this).attr("name")] = $(this).val();
                    return;
                }
                return;
            }
            ret[$(this).attr("name")] = $(this).val();
            return;            
        }
        if($(this).get(0) instanceof HTMLSelectElement) {
            ret[$(this).attr("name")] = $(this).val();
            return;
        }
        if($(this).get(0) instanceof HTMLImageElement){
            ret[$(this).attr("name")] = $(this).attr('srca');
            return;
        }
        if($(this).attr($(this).attr("name")) != undefined) {
            ret[$(this).attr("name")] = $(this).attr($(this).attr("name"));
            return;
        } else {
            ret[$(this).attr("name")] = "";
        }

    });
    return ret;
}

function getParam(key) {
    const url = new URL(location.href);
    params = url.searchParams;
    return params.get(key);
}

function showToast(msg, color) {
    class Toast {
        constructor(msg, color) {
            this.msg = msg;
            this.color = color;         
            this.ele = document.createElement('div');
            this.state = "REG";
            this.ele.addEventListener('mouseover', () => {
                if(this.state == "CLOSING" || this.state == "OPENING") {                    
                    clearTimeout(this.fadeoutTimeout);
                    this.ele.classList.add('active');                    
                }
            });
            this.ele.addEventListener('mouseout', () => {
                if(this.state == "CLOSING" || this.state == "OPENING") {                    
                    this.fadeoutTimeout = setTimeout(() => {
                        this.ele.classList.remove('active');
                        this.ele.addEventListener('transitionend', () => {
                            this.ele.remove();
                            this.state = "CLOSED";
                        }
                        );
                    }, 2000);
                }
            });
            this.ele.addEventListener('fadein', () => {
                this.state = "OPENING";
                this.fadeinTimeout = setTimeout(() => {
                    this.ele.classList.add('active');
                    this.ele.dispatchEvent(new Event('fadeout'));
                }, 0); 
            });
            this.ele.addEventListener('fadeout', () => {
                this.state = "CLOSING";
                this.fadeoutTimeout = setTimeout(() => {
                    this.ele.classList.remove('active');
                    this.ele.addEventListener('transitionend', () => {
                        this.ele.remove();
                        this.state = "CLOSED"
                    });
                }, 2000);
            });
            this.ele.classList.add('toast');                        
            this.ele.innerHTML = this.msg;
            this.ele.style.backgroundColor = this.color;
            document.body.append(this.ele);            
            this.ele.dispatchEvent(new Event('fadein'));
        }
        
    }
    new Toast(msg, color);    
}

function showAside(url, callback) {    
    var aside = $('aside');
    aside.load(url, callback);        
    aside.show();
    aside.css('margin-right', '0'); 
    aside.addClass('active');
    $('aside').off().on('transitionend', function(){
        try {onShowAside()}catch{}        
    })   
    
}

function hideAside() {     
    var aside = $('aside');   
    aside.css('margin-right', '-400px');   
    aside.removeClass('active');
    aside.off().on('transitionend', function(){        
        try {onHideAside()}catch{}
    });    
}

function deleteColorView(ele) {
    $(ele).closest('item').remove();
}

function getImage(v, d, e) {
    var type = currentUser.auth ? currentUser.auth : $('#app').attr('user');
    if(v && isImageFile(v)) {                
        return "" + IMG_SERVER + type + "/file" + v;
    }        
    return '/img/noimage.jpg';
}

function isImageFile(src) {
    const srcArr = src.split(".");
    const ext = srcArr[srcArr.length - 1];
    if(["apng", "bmp", "svg", "webp", "png", "jpg", "jpeg", "gif", "pdf"].includes(ext)) { return true }
    return false;
}

function formatEntryListFromDataTransferItemList(items) {
    let ret = [];
    Array.from(items).forEach(item => { ret.push(item.webkitGetAsEntry())})
    return ret;
}

function getTreeFromEntrylist(list, rel, depth) {
    let ret = [];
    var r = rel ? rel : -1;
    var d = depth ? depth : 0;
    list.forEach(item => {
        var obj = formatDataFromItem(item);        
        obj.entrypkey = getOldEntryNum(obj);
        obj.entrynum = getEntryNum(obj);
        obj.parentnum = r;
        obj.rel = r;
        obj.depth = d;  
        obj.size = 0;
        obj.modifydate = 0;      
        if(item.isFile) {
            item.file(file => {
                obj.size = file.size;
                obj.modifydate = file.lastModified;
            });                
        }
        if(item.isDirectory) {
            const reader = item.createReader();
            reader.readEntries(subentry => {
                obj.entrylist = getTreeFromEntrylist(subentry, obj.entrynum, d+1);
            });
        }
        ret.push(obj);
    })
    return ret;
}

function setCurrentEntryNum(entrynum) {
    if(currentEntryNum <= parseInt(entrynum)) {
        currentEntryNum = entrynum;
    }
}

function getCurrentEntryNum() {
    return parseInt(currentEntryNum);
}

function getOldEntryNum(obj) {
    let num = -1;
    if($('.fileTreeViewerFragment')) {
        Array.from($('.fileTreeViewerFragment .treeItem')).forEach(olditem => {
            if(olditem.binddata.path == obj.path) { num = olditem.binddata.entrypkey }
        });
    }    
    return num;
}

function getEntryNum(obj) {
    if(obj.entrypkey == -1) {
        setCurrentEntryNum(getCurrentEntryNum()+1);
        return getCurrentEntryNum();
    }
    return obj.entrypkey;
}

function formatDataFromItem(item) {    
    return obj = {
        name : item.name,
        isfile : item.isFile ? "Y" : "N",
        path : item.fullPath,
        entry : item        
    };    
}

function getTreeFromArray(list, rel, depth, parentPath) {    
    let ret = [];
    var r = rel ? rel : -1;
    var d = depth ? depth : 0;    
    list.forEach(item => {
        if(item.rel == r) {
            item.path = parentPath ? parentPath + "/" + item.name : "/" + item.name; 
            item.depth = d;
            item.entrylist = getTreeFromArray(list, item.entrypkey, d+1, item.path);
            ret.push(item);
        }            
    });
    return ret;
}

function getUniqueList(list, key) {
    let ret = [];
    let uniqVal = []; 
    for(let i=0;i < list.length;i++) {
        if(!uniqVal.includes(list[i][key])) {
            uniqVal.push(list[i][key]);
            ret.push(list[i]);
        }
    }
    ret.sort((a,b) => { return a[key] < b[key] ? -1 : 1 });
    return ret;

}

function getChildList(parentlist, childlist, sortkey) {
    let ret = [];            
    parentlist.forEach(item => {                
            ret = ret.concat(item[childlist]);
            if(sortkey) { ret.sort((a, b) => { return parseInt(a[sortkey])  > parseInt(b[sortkey]) ? 1 : -1 }); } 
        });            
    return ret;
}

function getValueArray(list, key) {
    let ret = [];
    list.forEach(item => {
         ret.push(item[key]);
    })
    return ret;
}

function getCopyObject(obj) {    
    if (obj === null || typeof obj !== "object") {
        return obj;
    }    
    const ret = Array.isArray(obj) ? [] : {};    
    for (let key of Object.keys(obj)) {
        ret[key] = getCopyObject(obj[key]);
      }    
    return ret;
}

function formatDateNum(v, d, e) {    
    if( v == "99991231") { return "--" }    
    const yyyy = v.substring(0, 4);
    const mm = v.substring(4, 6);
    const dd = v.substring(6, 8);
    return `${yyyy}-${mm}-${dd}`;
}

function formatEpisodeStatus(v, d, e) {
    if(v == "REG") { return "준비"; }
    if(v == "NORMAL") { return "시작"; }
    if(v == "INVISIBLE") { return "삭제"; }
    return v;
}

function formatTaskStatus(v, d, e) {
    $(e).addClass(v);
    $(e).addClass('status');
    if(v == "REG") { return "준비"; }
    if(v == "ING") { return "진행"; }
    if(v == "DONE") { return "완료"; }
    return v;
}

function formatWorkStatus(v, d, e) {
    $(e).addClass(v);
    $(e).addClass('status');
    if(v == "REG") {return "수락대기"; }
    if(v == "ACCEPT") { return "수락"; }    
    if(v == "ING") { return "진행"; }
    if(v == "DONE") { return "완료"; }
    if(v == "FAIL") { return "부분완료"; }
    if(v == "CANCEL") { return "취소"; }
    return v;
}

function formatPriority(v, d, e) {
    if(v == "100") { return "낮음"; }
    if(v == "200") { return "보통"; }    
    if(v == "300") { return "높음"; }
    return v;
}

function formatLength(v, d, e) {
    return formatNumber(v.length);
}

function formatProgress(v, e, d) {    
    return v + "%";
}

function formatTaskKind(v, d, e) {
    if(v == "A") { return "밑색" }
    if(v == "B") { return "채색" }
    if(v == "ETC") { return "기타" }
    return v;
}

function formatTeamStatus(v, d, e) {
    if(v == "NORMAL") { return "운영중" }
    if(v == "NOTALLOWED") { return "제한됨" }
    return v;
}

function formatMemberStatus(v, d, e) {
    if(v == "NORMAL") { return "소속" }
    if(v == "AWARE") { return "배정중단" }
    if(v == "REQKICK") { return "탈퇴요청" }
    if(v == "NOTTEAM") { return "탈퇴" }
    return v;
}

function formatFileCommitKind(v, d, e) {
    if(v == "TM") {return "assignment_ind"}
    if(v == "ME") {return "person"}
}

function formatHexCode(colorArray) {
    let ret = "#";
    for(let i = 0; i < colorArray.length; i++) {
        ret = ret + ("00"+ colorArray[i].toString(16)).slice(-2);
    }
    return ret;
}

function formatHexCodeName(v, d, e) {
    return "#" + v;
}

function fommatCategory(v, d, e) {
    if(v == '1') {return "얼굴"}
    if(v == '2') {return "헤어"}
    if(v == '3') {return "기타"}
}

function formatIsFile(v, d, e) {
    if(v == "Y") { return "파일" }
    if(v == "N") { return "폴더" }
}

function getLength(list, key) {
    let len = 0;
    for(i = 0; i< list.length; i++) {
        if(list[i]["kind"] == key) { len ++ }
    }        
    return len;
}

function formatEntrySize(v, d, e) {    
    if(v == undefined) { return }
    if(v > 1048576) { return formatNumber(Math.round(v / 1048576)) + "MB"}
    if(v > 1024) { return formatNumber(Math.round(v / 1024)) + "KB"}
    return v + "B";
}

function formatNumber(v, d, e) {
    return v.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function formatWorkCauseKindIcon(v, d, e) {
    if(d.workcausestatus == "REJECTED") { $(e).css('background-color', "var(--theme-color-alt-b)")}
    if(d.workcausestatus == "PENDING") { $(e).css('background-color', "var(--theme-color-prime)")}
    if(d.workcausestatus == "DONE") { $(e).css('background-color', "var(--theme-color-alt-a)")}
    return formatWorkCauseKindName(v);
}

function formatWorkCauseKindName(v) {
    if(v == "SCHEDULE") { return "schedule" }
    if(v == "CANCEL") { return "front_hand" }
    if(v == "RESULT") { return "receipt_long" }    
}

function formatWorkCauseKind(v, d, e) {
    if(v == "SCHEDULE") { return "종료일 변경" }
    if(v == "CANCEL") { return "작업 취소" }
    if(v == "RESULT") { return "결과 검토" }
}

function formatEntryIcon(v, d, e) {
    if(v == "Y") { return "description" }
    if(v == "N") { return "folder" }
}

function formatCheckView(v, d, e) {
    if(v == "N") { return "notifications_active" }
    return "";
}

function setVisibility(v, d, e) {
    if(!v) { $(e).hide(); }
    else { $(e).show() }
    return v
}

function drawColor(v, d, e) {
    $(e).css('background-color', "#" + v);
    return v;
}

function isValidColorHex(value) {
    if(value.length != 8) { return false; }
    if(value.match(/[^0-9A-Fa-f]/gi)) { return false; }        
    const r = value.substring(0, 2);
    const g = value.substring(2, 4);
    const b = value.substring(4, 6);
    const a = value.substring(6);
    const arr = [r, g, b, a];
    for(i=0; i < arr.length; i++) {            
        if(arr[i].length != 2) { return false; }
        const int = parseInt(arr[i], 16);
        console.log(int);
        if(int < 0 || int > 255) { return false; }
    }
    return true;
}

function bindValue(ele, target, attr) {
    switch (attr) {
        case 'text' : { target.textContent = ele.value; break ;}
    }
    
}

function sendFileWithInput(progressCallback, retfn, API) {
    var el = document.createElement("input");
    el.setAttribute("type", "file");    
    el.addEventListener("change", e => {
        sendFile(el.files[0], progressCallback, retfn, API);
    });
    el.click();
    el.remove();
};

function sendFile(file, progressCallback, retfn) {
    const uri =  IMG_SERVER + currentUser.auth + "/uploadFile.json";
    const xhr = new XMLHttpRequest();
    const fd = new FormData();
    xhr.upload.addEventListener("progress", function(e) {
        if (e.lengthComputable) {
            const percentage = Math.round((e.loaded * 100) / e.total);
            progressCallback(percentage);
        }
    }, false);
    
    xhr.open("POST", uri, true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            try {
                var ret = JSON.parse(xhr.responseText);
                if(ret.head.status=="ok") { retfn(ret.body.filename); return; }
            } catch(e) { retfn(null); }    
        }
    };
    
    fd.append('file', file);    
    xhr.send(fd);
}

function sendFileEntry(file, progressCallback, retfn, uploadToken, entrynum, api) {
    const uri =  IMG_SERVER + currentUser.auth + api;
    const xhr = new XMLHttpRequest();
    const fd = new FormData();
    
    xhr.upload.addEventListener("progress", function(e) {
        if (e.lengthComputable) {
            const percentage = Math.round((e.loaded * 100) / e.total);
            progressCallback(entrynum, percentage);
        }
    }, false);
    
    xhr.open("POST", uri, true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            try{
                var ret = JSON.parse(xhr.responseText);
                if(ret.head.status=="ok"){
                    retfn(entrynum);
                    return;
                }
            }catch(e){                
                retfn(entrynum, e);                
            }
    
        }
    };
    
    
    fd.append('file', file);    
    fd.append('t', currentUser.t);
    fd.append('uploadtoken', uploadToken);
    fd.append('kind', currentUser.auth);
    xhr.send(fd);
}

function downloadFile(entryversionpkey) {
    var el = document.createElement('a');
    el.href = IMG_SERVER + currentUser.auth + "/file/downloadFile.json?kind=" + currentUser.auth + "&entryversionpkey=" + entryversionpkey;
    el.click();
    el.remove();
}

function downloadFolder(entrypkey, updatesetpkey, workpkey) {
    if(entrypkey == -1) { return; }    
    var el = document.createElement('a');     
    el.href = IMG_SERVER + currentUser.auth + "/file/downloadDirectory.json?entrypkey=" + entrypkey + "&updatesetpkey=" + updatesetpkey + "&workpkey=" + workpkey;
    el.click();
    el.remove();
}

function downloadTaskFile(taskentrypkey) {
    const el = document.createElement('a');
    el.href = IMG_SERVER + currentUser.auth + "/file/downloadTaskEntryFile.json?taskentrypkey=" + taskentrypkey;
    el.click();
    el.remove();
}

function downloadTaskFolder(taskentrypkey) {
    if(taskentrypkey == -1) { return; }    
    var el = document.createElement('a');     
    el.href = IMG_SERVER + currentUser.auth + "/file/downloadTaskEntryDirectory.json?taskentrypkey=" + taskentrypkey;
    el.click();
    el.remove();
}

function downloadTaskEntryAll(taskpkey) {        
    var el = document.createElement('a');
    el.href = IMG_SERVER + currentUser.auth + "/file/downloadTaskEntryAll.json?&taskpkey=" + taskpkey;
    el.click();
    el.remove();    
}

function downloadUpdateSet(updatesetpkey) {    
    var el = document.createElement('a');
    el.href = IMG_SERVER + currentUser.auth + "/file/downloadUpdateSet.json?&updatesetpkey=" + updatesetpkey;
    el.click();
    el.remove();    
}

function selectItem(ele, callback) {
    $(ele).siblings().removeClass('active');
    $(ele).addClass('active');
    if(callback) { callback(); }    
}

function appendTreeItem(entry, target, clone) {        
    bindView(clone, entry);            
    $(target).closest('item').append(clone);
}

function formatDFS(ds) {
    const yyyy = ds.substring(0, 4);
    const mm = ds.substring(4, 6);
    const dd = ds.substring(6, 8);   
    const date = new Date(yyyy, mm-1, dd);
    return date;
}

function formatDFSMonth(ds) {    
    const yyyy = ds.split("-")[0];
    const mm = ds.split("-")[1];
    const date = new Date(yyyy, mm-1, 1);
    return date
}

function formatDTS(date){    
    var ret = ""
    let d = new Date(date);
    const yyyy = d.getFullYear();
    const mm = ("0" + (d.getMonth() + 1)).slice(-2);
    const dd = ("0" + d.getDate()).slice(-2);
    ret = "" + yyyy + mm + dd;
    return ret;
}

function formatDTSShort(date){    
    var ret = ""
    let d = new Date(date);    
    const mm = ("0" + (d.getMonth() + 1)).slice(-2);
    const dd = ("0" + d.getDate()).slice(-2);
    ret = "" + mm + "/" + dd;
    return ret;
}

function formatDTSMonth(date){    
    var ret = ""
    let d = new Date(date);    
    const yyyy = d.getFullYear();
    const mm = ("0" + (d.getMonth() + 1)).slice(-2);    
    ret = "" + yyyy + "-" + mm;
    return ret;
}

function getRangedList(rangearr, list) {
    let ret = [];
    ret = list.filter(item => {
        return rangearr.includes(item.year + "-" + ("0" + item.month).slice(-2))
    });    
    return ret;
}

function getDateGap(sd, ed, fixtozero) {
    const MAX_DATE_GAP = 100;
    let ret;
    let start = formatDFS(sd);
    let end = formatDFS(ed);
    let sday = start.getTime() / (24*60*60*1000);
    let eday = end.getTime() / (24*60*60*1000);
    let gap = Math.round(eday - sday);
    ret = gap;
    if(!fixtozero) {
        ret = MAX_DATE_GAP
        if(gap < 100) { ret = gap };
        if(gap < 0) { ret = 0}
    }
    return ret;
};

function getRootStyle(key) {
    const root = document.querySelector(':root');
    const rs = getComputedStyle(root);
    const value = rs.getPropertyValue(key);        
    return value;
}

function onRangeInput(ele, e, id) {
    $(ele).css('background-size', (parseInt(e) + 1).toString() + '% 100%');
    const target = document.getElementById(id);
    target.textContent = e + "%"
}

function setRange(v, d, e) {    
    $(e).css('background-size', (parseInt(v) + 1).toString()  + '% 100%');
    return v;
}

function setRequestAlarm(v, d, e) {
    let kind;
    v.forEach(item => {
        if(item.workcausestatus == "PENDING") { kind = item.kind; }
    })
    if(kind == "RESULT") { return "receipt_long"; }
    if(kind == "CANCEL") { return "front_hand"; }
    if(kind == "SCHEDULE") { return "schedule"; }
    return "";
}

function dragOverHandle(e) {
    e.preventDefault();
}

function dragLeavehandle(e) {
    e.preventDefault();            
}

function dropHandle(e) {
    e.stopPropagation();     
    e.preventDefault();            
    if(e.dataTransfer && e.dataTransfer.items) {                
        onSelectedItems(e.dataTransfer.items);
    }
}

function setDuration(v, d, e) {
    const dateGap = getDateGap(formatDTS(new Date()), v, true);
    let msg;
    if(dateGap == 0) { msg = "오늘" ; return msg;}
    msg = dateGap + "일";
    return msg;
}

function setCellActive(v, d, e) {
    if(v != undefined && v != 0) {
        e.closest('.cell').classList.add('active');        
    }
    return v;
}

function isValid(data, condition, target) {
    if(condition == "empty") { 
        if(data == undefined || data == "") { return false; }
    }
    if(condition == "<=") {
        if(data <= target) { return false; }
    }
    if(condition == "<") {
        if(data < target) { return false; }
    }    
    return true;
}

function setFilteredIndex(key, id, data) {
    if(data[key]) {
            const options = $('#'+id).get(0).options;
            Array.from(options).forEach(option => {
                if(option.value == data[key]) {
                    $('#'+id).get(0).selectedIndex = option.index;                        
            }                    
        })                
    }
}

function setListScroll(ele) {        
    const container = $(ele).closest('.overflowContainer').get(0);        
    container.scrollTop = ele.offsetTop - container.offsetTop; 
}

function showSkeleton(seletor) {
    const target = document.querySelector(seletor);
    const skViews = target.querySelectorAll('.skeleton');
    skViews.forEach(view => {
        const count = parseInt(view.getAttribute('c'));            
        for(i=0; i < count; i++) {
            let clone = view.cloneNode(true);
            view.parentNode.appendChild(clone);                
        }
    })
}

class Modal {
    constructor(data) {
        this.data = data;     
        console.log(this.data);   
        this.ele = document.createElement('div');        
        this.ele.classList.add('modalView');     

        this.wrap = document.createElement('div');
        this.wrap.classList.add('modalWrap');
        this.ele.appendChild(this.wrap); 

        this.head = document.createElement('div');
        this.head.classList.add('modalHead');
        this.wrap.appendChild(this.head);

        this.headstart = document.createElement('span');
        this.headstart.classList.add('modalHeadStart');
        this.headstart.textContent = "Preview";
        this.head.appendChild(this.headstart);

        this.headend = document.createElement('span');
        this.headend.classList.add('modalHeadEnd');
        this.head.appendChild(this.headend);
        
        this.main = document.createElement('div');
        this.main.classList.add('modalMain');
        this.wrap.appendChild(this.main);

        this.close = document.createElement('span');
        this.close.classList.add('material-icons', 'modalClose');
        this.close.textContent = "close";
        this.close.addEventListener('click', () => {this.hide()});        
        this.headend.appendChild(this.close);

        this.content = document.createElement('div');
        this.content.classList.add('modalContent');
        this.main.appendChild(this.content);


        if(this.data.kind == "image" && this.data.src && isImageFile(this.data.src)) {
            this.canvas = document.createElement('canvas');
            this.canvas.classList.add('modalCanvas');
            this.content.appendChild(this.canvas);            
            this.drawCanvas(this.data.src);                        
        }

        if(this.data.kind == "pdf" && this.data.src) {

            this.embed = document.createElement('embed');
            this.embed.style.width = "100%";
            this.embed.style.height = "100%";
            this.embed.type = "application/pdf"
            this.embed.src = this.data.src + "#toolbar=0";
            this.content.appendChild(this.embed);
        }
        

        /* this.inner = document.createElement(this.data.kind);
        this.inner.classList.add('modalContentInner');
        this.inner.src = this.data.src;
        this.content.appendChild(this.inner); */
    }

    show() {
        document.body.appendChild(this.ele);
    }

    hide() {
        this.ele.remove();
    }

    drawCanvas(src) {
        
        this.ctx = this.canvas.getContext('2d');
        const img = new Image();        
        img.src = src;        
        img.onload = () => {
            this.canvas.width = img.width;
            this.canvas.height = img.height;
            this.ctx.drawImage(img, 0, 0, img.width, img.height);
            const rect = this.canvas.getBoundingClientRect();
        }
     
    }
}


window.addEventListener('DOMContentLoaded', function(){    
    init();
})