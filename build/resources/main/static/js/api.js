const TEST_MODE = true;

const IMG_SERVER = TEST_MODE ? "http://192.168.50.24:3000/" : "https://tms.webtoon.ai:3000/" ; 
const API_SERVER = TEST_MODE ? "http://192.168.50.24:3000/" : "https://tms.webtoon.ai:3000/" ; 


class Api {
    constructor(apikind) {
        this.apikind = apikind;
    }

    get(url, data) {
        let obj = {};
        try{obj.t = currentUser.t } catch { }
        if(data) { Object.assign(obj, data) }        
        let ret = new Promise( (res, rej) => {
            fetch(API_SERVER + this.apikind + url + this.getParam(obj)).then(response => {
                res(response.json());
            }).catch(err => { rej(); });
        });
        return ret;
    }
    

    post(url, data) {    
        let obj = {};
        try { obj.t = currentUser.t } catch {}
        if(data) {Object.assign(obj, data)}
        let option = {
            method : 'POST',
            headers : {
                'Content-Type': 'application/x-www-form-urlencoded'
            }, 
            body : new URLSearchParams(obj)
        }
        let ret = new Promise((res, rej) => {  
            fetch(API_SERVER + this.apikind + url, option).then(response => {
                res(response.json());
            }).catch(err => {
                this.rfn("알 수 없는 에러가 발생했습니다.");
                rej(); });            
        });
        return ret; 
    }

    getParam(obj) {
        let ret = "";
        if(obj == undefined) { return ret; }
        let paramString = "?" + new URLSearchParams(obj).toString();
        ret = paramString;
        return ret;
    }

    rtn(msg) {
        showToast(msg);
    }
}

class ComApi extends Api {
    constructor() {
        super("com/");        
    }

    /* get */
    getBoard(data) { return super.get("getDashboard.json", data) }    
    listTitleAll(data) {return super.get("listTitle.json", data)}
    listTitleVisible(data) {return super.get("listTitleVisible.json", data)}
    listEpisodeAll(data) {return super.get("listEpisodeAll.json", data)}
    listEpisodeVisible(data) {return super.get("listEpisodeVisible.json", data)}
    listTaskManager(data) {return super.get("listTaskManager.json", data)}
    listWorker(data) {return super.get("listWorker.json", data)}
    listCharacter(data) {return super.get("listCharacter.json", data)}
    getCharacter(data) {return super.get("getCharacter.json", data)}
    listWorkGroup(data) {return super.get("listWorkgroupForWorker.json", data)}
    listMember(data) {return super.get("listWorkerInTeam.json", data)}
    listTaskKindByEpisode(data) {return super.get("listTaskKindByEpisode.json", data)}
    listTaskEntry(data) {return super.get("listTaskEntry.json", data)}                                        

    /* post */
    getUser(data) { return super.post("getUser.json", data) }
    userLogin(data) { return super.post("login.json", data) }
    userLogout(data) { return super.post("logout.json", data) }
    checkToken(data) { return super.post("checkToken.json", data) }
    addTitle(data) { return super.post("addTitle.json", data) }
    modifyTitle(data) { return super.post("modifyTitle.json", data) }
    setTitleToNormal(data) { return super.post("setTitleStatusToNormal.json", data) }
    setTitleToInvisible(data) { return super.post("setTitleStatusToInvisible.json", data) }
    addEpisode(data) { return super.post("addEpisode.json", data) }
    modifyEpisode(data) { return super.post("modifyEpisode.json", data) }
    setEpisodeToReg(data) { return super.post("setEpisodeStatusToReg.json", data) }
    setEpisodeToNormal(data) { return super.post("setEpisodeStatusToNormal.json", data) }
    setEpisodeToInvisible(data) { return super.post("setEpisodeStatusToInvisible.json", data) }
    addTaskManager(data) { return super.post("addTaskManager.json", data) }
    modifyTaskManager(data) { return super.post("modifyTaskManager.json", data) }
    setTaskManagerToNotAllowed(data) { return super.post("setTaskManagerStatusToNotAllowed.json", data) }
    setTaskManagerToNormal(data) { return super.post("setTaskManagerStatusToNormal.json", data) }
    addWorker(data) { return super.post("addWorker.json", data) }
    addCharacter(data) { return super.post("addCharacter.json", data) }
    modifyCharacter(data) { return super.post("modifyCharacter.json", data) }                                    
    dismissWorkerFromWorkgroup(data) { return super.post("dismissWorkerToTaskManager.json", data) }                                    
    rejoinWorkerToWorkgroup(data) { return super.post("rejoinWorkerToWorkgroup.json", data) }                                    
    joinWorkerToTeam(data) { return super.post("assignWorkerToTaskManager.json", data) }                                    
    addStyle(data) { return super.post("addStyle.json", data) }                                    
    addColorOnPalette(data) { return super.post("addColorOnPalette.json", data) }                                    
    updatePalette(data) { return super.post("updatePalette.json", data) }                                                                                        
}

class TmApi extends Api {
    constructor() {
        super("tm/");        
    }

    /* get */
    getBoard(data) { return super.get("getDashboard.json", data) }    
    listTask(data) { return super.get("listTask.json", data) }        
    listTitle(data) { return super.get("listTitle.json", data) }        
    listEpisode(data) { return super.get("listEpisode.json", data) }        
    listMember(data) { return super.get("listMember.json", data) }        
    listNormalMember(data) { return super.get("listNormalMember.json", data) }        
    listCompanyCharacterStyle(data) { return super.get("listCompanyCharacterStyle.json", data) }        
    getWork(data) { return super.get("getWork.json", data) }        
    getWorkDetail(data) { return super.get("getWorkDetail.json", data) }        
    getTaskDetail(data) { return super.get("getTaskDetail.json", data) }        
    listEntryVersionCommit(data) { return super.get("listEntryVersionCommit.json", data) }        
    listCurrentEntry(data) { return super.get("listCurrentEntry.json", data) }        
    taskEntryCheckUploadDone(data) { return super.get("taskEntryCheckUploadDone.json", data) }        
    listTaskEntry(data) { return super.get("listTaskEntry.json", data) }        

    /* post */
    getUser(data) { return super.post("getUser.json", data) }        
    userLogin(data) { return super.post("login.json", data) }        
    userLogout(data) { return super.post("logout.json", data) }        
    checkToken(data) { return super.post("checkToken.json", data) }            
    addTask(data) { return super.post("addTask.json", data) }        
    deleteTask(data) { return super.post("deleteTask.json", data) }        
    setTaskStatusToDone(data) { return super.post("setTaskStatusToDone.json", data) }        
    addWork(data) { return super.post("addWork.json", data) }        
    modifyWork(data) { return super.post("modifyWork.json", data) }                
    cancelWork(data) { return super.post("cancelWork.json", data) }                
    setWorkPriority(data) { return super.post("setWorkPriority.json", data) }                
    rejectWorkResult(data) { return super.post("setWorkResultStatusToRejected.json", data) }                            
    doneCancelRequest(data) { return super.post("doneCancelRequest.json", data) }                            
    rejectCancelRequest(data) { return super.post("rejectCancelRequest.json", data) }                            
    doneWorkResult(data) { return super.post("setWorkResultStatusToDone.json", data) }                            
    setMemberStatusToAware(data) { return super.post("setMemberStatusToAware.json", data) }                            
    setMemberStatusToNormal(data) { return super.post("setMemberStatusToNormal.json", data) }                            
    setMemberStatusToReqKick(data) { return super.post("setMemberStatusToReqKick.json", data) }                            
    setWorkScheduleForWorkCauseStatusToRejected(data) { return super.post("setWorkScheduleForWorkCauseStatusToRejected.json", data) }                            
    setWorkScheduleForWorkCauseStatusToDone(data) { return super.post("setWorkScheduleForWorkCauseStatusToDone.json", data) }                            
    setWorkResultForWorkCauseStatusToRejected(data) { return super.post("setWorkResultForWorkCauseStatusToRejected.json", data) }                            
    setWorkResultForWorkCauseStatusToDone(data) { return super.post("setWorkResultForWorkCauseStatusToDone.json", data) }                            
    addWorkCharacterStyleFromCompanyCharacter(data) { return super.post("addWorkCharacterStyleFromCompanyCharacter.json", data) }                            
    addWorkCharacterStyle(data) { return super.post("addWorkCharacterStyle.json", data) }                            
    addColorOnPalette(data) { return super.post("addColorOnPalette.json", data) }                                                                                            
    uploadFileTaskEntry(data) { return super.post("uploadFileTaskEntry.json", data) }                                                                                            
    addEntry(data) { return super.post("addEntry.json", data) }                                                                                            
    checkUploadDone(data) { return super.post("checkUploadDone.json", data) }                                                                                                                                                                   
}

class MeApi extends Api {
    constructor() {
        super('me/');        
    }

    /* get */
    getBoard(data) { return super.get("getDashboard.json", data) }    
    listWorkToDo(data) { return super.get("listWorkToDo.json", data) }    
    listWorkGone(data) { return super.get("listWorkGone.json", data) }    
    getWorkDetail(data) { return super.get("getWorkDetail.json", data) }    
    listEntryVersionCommit(data) { return super.get("listEntryVersionCommit.json", data) }    
    checkUploadDone(data) { return super.get("checkUploadDone.json", data) }    
                        
    /* post */
    getUser(data) { return super.post("getUser.json", data) }    
    userLogin(data) { return super.post("login.json", data) }    
    userLogout(data) { return super.post("logout.json", data) }    
    checkToken(data) { return super.post("checkToken.json", data) }    
    addWorkResult(data) { return super.post("addWorkResult.json", data) }    
    addWorkSchedule(data) { return super.post("addWorkSchedule.json", data) }    
    acceptWork(data) { return super.post("acceptWork.json", data) }    
    requestCancelWork(data) { return super.post("requestCancelWork.json", data) }    
    updateWorkProgress(data) { return super.post("updateWorkProgress.json", data) }    
    addEntry(data) { return super.post("addEntry.json", data) }                            
}

const COMAPI = new ComApi();
const TMAPI = new TmApi();
const MEAPI = new MeApi();