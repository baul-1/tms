class Aside {
    constructor() {
        
    }

    load(url, callback) {
        console.log('showAside', url);
        this.aside = $('aside');        
        this.aside.load(url, callback);        
        this.aside.show();
        this.aside.css('margin-right', '0'); 
        this.aside.addClass('active');
        this.aside.off().on('transitionend', function() { try { onToggleAside() }catch{}});
    }
    

    createTitle(callback) { this.load('../aside/createtitle.html', callback); }
    detailTitle(callback) { this.load('../aside/detailtitle.html', callback); }
    detailEpisode(callback) { this.load('../aside/detailepisode.html', callback); }
}

var ASIDE = new Aside();