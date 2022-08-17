class Vinder {
    constructor(id, config) {
        var _config = {
            vindlistitemtag : "v-item",
            vindempty : "v-empty",
            vinddone : "v-done"
        }
        this.id = id;
        this.ele = document.getElementById(id);
        this.config = config ? config : _config;
        this.templates = [];
        this.emptylist = [];        
        this.init();
    }

    init() {
        this.findListItems(this.ele);    
        this.findListEmptyItems(this.ele);        
    }    


    getConfig() { return this.config; }

    setConfig(k, v) { this.config[k] = v; }    

    findListEmptyItems(ele) {
        var items = ele.querySelectorAll('[' + this.config.vindempty + ']');
        if (items.length == 0) return;

        for (let i = 0; i < items.length; i++) {
            let item = items[i];                
            let innerItems = item.querySelectorAll('[' + this.config.vindempty + ']');                
            if (innerItems.length <= 0) {
                const parent = item.parentNode;
                const itemview = parent.removeChild(item);
                this.emptylist.push(itemview);                
                parent.emptylistid = this.emptylist.length -1;
            }     
        }
        this.findListItems(ele);
    }

    

    findListItems(ele) {            
        var items = [];
        if(ele.hasAttribute(this.config.vindlistitemtag)) { items.push(ele) }
        items = items.concat(Array.from(ele.querySelectorAll('[' + this.config.vindlistitemtag + ']'))) ;
        if (items.length == 0) return;

        for (let i = 0; i < items.length; i++) {
            let item = items[i];                
            let innerItems = item.querySelectorAll('[' + this.config.vindlistitemtag + ']');                
            if (innerItems.length <= 0) {
                const parent = item.parentNode;
                const itemview = parent.removeChild(item);
                this.templates.push(itemview);                
                parent.templateid = this.templates.length -1;
            }     
        }
        this.findListItems(ele);            
    }

    clear() {
        const items = this.ele.querySelectorAll('[' + this.config.vindlistitemtag + ']'); 
        const empties = this.ele.querySelectorAll('[' + this.config.vindempty + ']'); 
        items.forEach(item => {item.remove();});
        empties.forEach(empty => {empty.remove();});
        
    }

    vind(data, ele) {            
        var vinds = [];
        if(ele && ele.hasAttribute('vind')) {
            vinds.push(ele)
            vinds = vinds.concat(Array.from(ele.querySelectorAll('[vind]'))); 
        } else {
            vinds = this.ele.querySelectorAll('[vind]')
        }        
        Array.from(vinds).forEach(element => {
            const commands = element.getAttribute('vind').split(',');
            commands.forEach(com => {
                var comarr = com.split(':');
                if(comarr.length < 2) return;

                var comkey = comarr[0];
                var comtype = "nomap"; 
                var val = undefined;
                try{ val = eval(`data.${comkey}`) } catch { console.warn(`no data for ${comkey}`); }
                if(!val) return;
                var comfnname = comarr[2];
                if(comfnname) {                    
                    try {
                        const fn = eval(comfnname);
                        val = fn(val, data, element);                 
                    } catch(e) {console.warn(`${e} : fnname => ${comfnname}, val => ${val}, data => ${data}`)}
                }                              
                element.vinddata = val;
                comtype = comarr[1];
                this.map(comtype, val, element);                    
            })    
        });            
    }
    
    map(t, v, e) {
        switch(t) {
            case 'text' : {
                e.textContent = v;
                break;
            }
            case 'class' : {
                e.classList.add(v);
            }
            case 'list' : {                
                if(!Array.isArray(v)) break;
                if(v.length <= 0) { this.onListEmpty(t, v, e); break; }
                var temp = this.templates[e.templateid];
                for(let i=0; i < v.length; i++) {
                    try {
                        const clone = temp.cloneNode(true);
                        this.vind(v[i], clone);                                                
                        e.appendChild(clone);
                    } catch { }
                }
            }
            case 'nomap' : {
                break;
            }
            default : {
                e.setAttribute(t, v);
            }
        }
        e.setAttribute(this.config.vinddone, "Y");   
    }

    onListEmpty(t, v, e) {
        try { e.appendChild(this.emptylist[e.emptylistid]) }
        catch {}        
    }
}