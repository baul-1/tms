class Gantt {
    constructor(config) {
        this.config = config;
    }

    render(target) {
        /* gantt */
        this.isScrollMode = false;
        this.eventStart = {x : 0, y : 0};
        this.gantt = document.createElement('div');        
        this.gantt.classList.add('gantt');                                

        /* ganttContainer */
        this.ganttContainer = document.createElement('div');
        this.ganttContainer.classList.add('ganttContainer');
        this.ganttContainer.style.width = this.config.option.days * this.config.option.datewidth + "px" ;    
        this.ganttContainer.addEventListener('mousedown', function(event){
            this.isScrollMode = true;
            this.eventStart = {x : event.x, y : event.y};            
        });
        this.ganttContainer.addEventListener('mousemove', function(event){
            if(this.isScrollMode) {                
                if(this.eventStart.x - event.x > 0) {                    
                    this.closest('.gantt').scrollLeft += 40;                    
                }
                if(this.eventStart.x - event.x < 0) {
                    this.closest('.gantt').scrollLeft -= 40;
                }
                this.eventStart.x = event.x;
            }            
        });
        this.ganttContainer.addEventListener('mouseup', function(){
            this.isScrollMode = false;            
        })    
        this.ganttContainer.appendChild(new GanttHeader(this.config));
        this.ganttContainer.appendChild(new GanttMain(this.config));        
        
        

        this.gantt.appendChild(this.ganttContainer);        

        this.gantt.addEventListener('scroll', () => {            
            if(this.gantt.scrollLeft <= 0) {                
                this.config.events.nav.scrollstart();
                return;
            }
            if(this.gantt.scrollLeft >= this.gantt.scrollWidth - this.gantt.clientWidth) {                
                this.config.events.nav.scrollend();
                return;
            }
            this.config.events.nav.scroll();
        });

        target.appendChild(this.gantt);        

        this.gantt.querySelectorAll('.ganttDivider').forEach(ele => {
            ele.style.height = this.gantt.querySelector('.ganttBody').clientHeight + "px";
        })        
        return;
    }
}

class GanttHeader {
    constructor(config) {
        this.ele = document.createElement('div');
        this.ele.classList.add('ganttHeader');
        for(let i=0; i<config.option.days; i++) {
            this.ele.appendChild(new GanttDate(i, config));
        }
        return this.ele;
    }
}

class GanttMain {
    constructor(config) {
        this.ele = document.createElement('div');
        this.ele.classList.add('ganttMain');        
        this.ele.appendChild(new GanttBase(config));        
        this.ele.appendChild(new GanttBody(config));
        return this.ele;
    }
}

class GanttBase {
    constructor(config) {        
        this.ele = document.createElement('div');
        this.ele.classList.add('ganttBase'); 
        for(let i=0; i<config.option.days; i++) {
            this.ele.appendChild(new GanttDivider(i, config));
        }            
        return this.ele;
    }
}

class GanttDivider {
    constructor(idx, config) {
        this.ele = document.createElement('div');
        this.ele.classList.add('ganttDivider');
        var date = formatDFS(config.option.firstdate);
        date.setDate(date.getDate() + parseInt(idx));
        if(date.getDay() == 0) { this.ele.classList.add('SUN')} 
        if(date.getDay() == 6) { this.ele.classList.add('SAT')} 
        if(formatDTS(date) == config.option.today) { this.ele.classList.add('TODAY');}
        this.ele.setAttribute('day', date.getDay()); 
        this.ele.setAttribute('date', formatDTS(date));        
        return this.ele;
    }
}

class GanttBody {
    constructor(config) {
        this.ele = document.createElement('div');
        this.ele.classList.add('ganttBody');
        for(let i=0; i<config.data.groups.length; i++) {            
            this.ele.appendChild(new GanttGroup(config.data.groups[i], config));
        }
        var groups = this.ele.querySelectorAll('.ganttGroup');
        Array.from(groups).forEach(group => {
            if(group.childNodes.length <= 0) { group.remove();}
        })        
        return this.ele;
    }
}

class GanttGroup {
    constructor(item, config) {                
        this.ele = document.createElement('div');
        this.ele.classList.add('ganttGroup'); 
        this.ele.item = item;
        
        for(var i=0; i<item.eventlist.length; i++) {
            if(getDateGap(config.option.firstdate, item.eventlist[i].enddate) > 0 && getDateGap(config.option.firstdate, item.eventlist[i].startdate) < config.option.days) {
                this.ele.appendChild(new GanttEventBar(item.eventlist[i], config));
            }            
        }        

        if(getDateGap(config.option.firstdate, item.enddate) >= 0 && getDateGap(config.option.firstdate, item.startdate) < config.option.days) {
            this.ele.appendChild(new GanttGroupExtendedContainer(item, config, this.getLatestEventEnddate(item.eventlist)));
        }
        

        if(getDateGap(config.option.firstdate, item.enddate) >= 0 && getDateGap(config.option.firstdate, item.startdate) < config.option.days) {
            this.ele.appendChild(new GanttGroupContainer(item, config));
        }
        this.ele.addEventListener('dblclick', function(){
            config.events.group.onclick(item);
        });      
        return this.ele;
    }

    getLatestEventEnddate(eventlist) {
        let lastdate = "0";
        for(var i=0; i < eventlist.length; i++) {
            if(formatDFS(lastdate).getTime() <= formatDFS(eventlist[i].enddate).getTime()) { lastdate = eventlist[i].enddate }            
        }
        return lastdate;
    }
}

class GanttGroupInfo {
    constructor(item, config) {
        console.log(item);
        this.item = item;
        this.config = config;
        this.ele = document.createElement('div');        
        this.ele.classList.add('ganttGroupInfo');

        this.infoname = document.createElement('span');
        this.infoname.textContent = item.taskname;
        this.ele.appendChild(this.infoname);

        this.ele.style.backgroundSize = this.item.progress + "% 100%"; 
        return this.ele;      
    }
}

class GanttDate {
    constructor(dategap, config) {
        const firstdate = formatDFS(config.option.firstdate);                
        let date = new Date(firstdate.getFullYear(), firstdate.getMonth(), firstdate.getDate());        
        this.ele = document.createElement('div');
        this.ele.classList.add('ganttDate'); 
        this.ele.textContent = formatDTSShort(date.setDate(firstdate.getDate() + dategap));
        if(date.getDay() == 0) { this.ele.classList.add('SUN')};
        if(date.getDay() == 6) { this.ele.classList.add('SAT')};
        if(formatDTS(date) == config.option.today) {
            this.ele.textContent = "오늘";
            this.ele.classList.add('today');
        }        
        return this.ele;
    }    
}

class GanttGroupContainer{
    constructor(item, config) { 
        this.item = item;
        this.config = config;       
        this.ele = document.createElement('div');        
        this.ele.classList.add('ganttGroupContainer');        
        this.setStyle();
        this.ele.addEventListener('click', function(){
            config.events.group.onclick(item);
        });
        this.ele.appendChild(new GanttGroupInfo(item, config));
        return this.ele;
    }

    setStyle() {
        const totaldays = this.getLeft() + this.getWidth();
        this.ele.style.left = this.getLeft() * this.config.option.datewidth +"px";
        this.ele.style.width = totaldays < this.config.option.days ? this.ele.style.width = this.getWidth() * this.config.option.datewidth + "px" : (this.config.option.days - this.getLeft()) * this.config.option.datewidth + "px";
    }

    getLeft() {
        const dategap = getDateGap(this.config.option.firstdate, this.item.startdate);        
        return dategap;
    }

    getWidth() {        
        const firstDate = formatDFS(this.config.option.firstdate).getTime() > formatDFS(this.item.startdate).getTime() ? this.config.option.firstdate : this.item.startdate;        
        const dategap = getDateGap(firstDate, this.item.enddate, true);                 
        if(dategap >= 0) { return dategap + 1 };
        return 0;        
    }
}

class GanttGroupExtendedContainer {
    constructor(item, config, lastdate) {
        this.item = item;
        this.config = config;
        this.lastdate = lastdate;
        this.ele = document.createElement('div');
        this.ele.classList.add('ganttGroupExtendedContainer');
        this.setStyle();
        return this.ele;
    }

    setStyle() {
        const totaldays = this.getLeft() + this.getWidth();
        this.ele.style.left = this.getLeft() * this.config.option.datewidth +"px";
        this.ele.style.width = totaldays < this.config.option.days ? this.ele.style.width = this.getWidth() * this.config.option.datewidth + "px" : (this.config.option.days - this.getLeft()) * this.config.option.datewidth + "px";
    }

    getLeft() {
        const dategap = getDateGap(this.config.option.firstdate, this.item.startdate);        
        return dategap;
    }

    getWidth() {        
        const firstDate = formatDFS(this.config.option.firstdate).getTime() > formatDFS(this.item.startdate).getTime() ? this.config.option.firstdate : this.item.startdate;        
        const lastDate = formatDFS(this.item.enddate).getTime() >= formatDFS(this.lastdate).getTime() ? this.item.enddate : this.lastdate;
        const dategap = getDateGap(firstDate, lastDate, true);                 
        if(dategap >= 0) { return dategap + 1 };
        return 0;        
    }
}

class GanttEventBar {
    constructor(item, config){
        this.item = item;
        this.config = config;                
        this.ele = document.createElement('div');
        this.ele.classList.add('ganttEventBar');
        this.ele.classList.add(item.status);
        if(this.hasPendingReq(item.workcauselist)) { this.ele.classList.add('req')}
        this.ele.item = item;        
        this.setStyle();
        this.ele.addEventListener('click', function(e){
            e.stopPropagation();
            config.events.event.onclick(item);
        });                    
        this.ele.appendChild(new GanttEventBarInfo(this.item));
        return this.ele;
    }       

    setStyle() {
        const totaldays = this.getLeft() + this.getWidth();
        this.ele.style.marginLeft = this.getLeft() * this.config.option.datewidth +"px";
        this.ele.style.width = totaldays < this.config.option.days ? this.ele.style.width = this.getWidth() * this.config.option.datewidth + "px" : (this.config.option.days - this.getLeft()) * this.config.option.datewidth + "px";
        this.ele.style.backgroundSize = this.item.progress + "% 100%";                
    }
    

    getLeft() {        
        const dategap = getDateGap(this.config.option.firstdate, this.item.startdate);        
        return dategap;
    }

    getWidth() {
        const firstDate = formatDFS(this.config.option.firstdate).getTime() >= formatDFS(this.item.startdate).getTime() ? this.config.option.firstdate : this.item.startdate;
        const dategap = getDateGap(firstDate, this.item.enddate);        
        return dategap + 1;
    }

    hasPendingReq(causelist) {
        const pendingReq = causelist.find(cause => {return cause.workcausestatus == "PENDING"});
        if(pendingReq) { return true };
        return false;
    }
}

class GanttEventBarInfo {
    constructor(item) {
        this.ele = document.createElement('div');
        this.ele.classList.add('info');                


        this.infoname = document.createElement('span');
        this.infoname.classList.add('infoName');
        this.infoname.textContent = item.name;
        this.ele.appendChild(this.infoname);

        if(item.workcauselist.length > 0 && this.hasPendingReq(item.workcauselist)) {
            this.inforeq = document.createElement('span');
            this.inforeq.classList.add('infoReq', 'material-icons');
            this.inforeq.textContent = this.getPendingReqKind(item.workcauselist);
            this.ele.appendChild(this.inforeq);
        }

        return this.ele;
    }

    hasPendingReq(causelist) {
        const pendingReq = causelist.find(cause => {return cause.workcausestatus == "PENDING"});
        if(pendingReq) { return true };
        return false;
    }

    getPendingReqKind(causelist) {
        const pendingReq = causelist.find(cause => {return cause.workcausestatus == "PENDING"});
        return formatWorkCauseKindName(pendingReq.kind);
    }
}
