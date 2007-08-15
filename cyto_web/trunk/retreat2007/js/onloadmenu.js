//GO1.1


///////////////////////////////////////
//
//  Generic onload by Brothercake
//  Necessary when multiple functions depend on the windows.onload or body.onload
//  http://www.brothercake.com/
//
///////////////////////////////////////

//onload function; call other functions from this
function generic()
{
	//create a new menu ('menu-id', 'orientation')
	var verticals = new simpleMenu('menu-v', 'vertical');
	var horizontals = new simpleMenu('menu-h', 'horizontal');
	
	
};

//setup onload function
if(typeof window.addEventListener != 'undefined')
{
	//.. gecko, safari, konqueror and standard
	window.addEventListener('load', generic, false);
}
else if(typeof document.addEventListener != 'undefined')
{
	//.. opera 7
	document.addEventListener('load', generic, false);
}
else if(typeof window.attachEvent != 'undefined')
{
	//.. win/ie
	window.attachEvent('onload', generic);
}

//** remove this condition to degrade older browsers
else
{
	//.. mac/ie5 and anything else that gets this far
	
	//if there's an existing onload function
	if(typeof window.onload == 'function')
	{
		//store it
		var existing = onload;
		
		//add new onload handler
		window.onload = function()
		{
			//call existing onload function
			existing();
			
			//call generic onload function
			generic();
		};
	}
	else
	{
		//setup onload function
		window.onload = generic;
	}
}

//menu object constructor
function simpleMenu(navid, orient)
{
	//if the dom is not supported, or this is opera 5 or 6, don't continue
	if(typeof document.getElementById == 'undefined' || /opera[\/ ][56]/i.test(navigator.userAgent)) { return; }
	
	//identify konqueror
	this.iskde = navigator.vendor == 'KDE';

	//identify internet explorer [but both opera and konqueror recognise the .all collection]
	this.isie = typeof document.all != 'undefined' && typeof window.opera == 'undefined' && !this.iskde;

	//identify old safari [< 1.2]
	this.isoldsaf = navigator.vendor == 'Apple Computer, Inc.' && typeof XMLHttpRequest == 'undefined';

	//ul tree
	this.tree = document.getElementById(navid);
	
	//if it exists
	if(this.tree != null)
	{
		//get trigger elements
		this.items = this.tree.getElementsByTagName('li');
		this.itemsLen = this.items.length;

		//initialise each trigger, using do .. while because it's faster
		var i = 0; 
		do
		{
			this.init(this.items[i], this.isie, this.isoldsaf, this.iskde, navid, orient);
		}
		while (++i < this.itemsLen);
	}
}


//trigger initialiser
simpleMenu.prototype.init = function(trigger, isie, isoldsaf, iskde, navid, ishoriz)
{
	//store menu object, or null if there isn't one
	//extend it as a property of the trigger argument
	//so it's global within [and unique to] the scope of this instantiation
	//which is the same trick as going "var self = this"
	trigger.menu = trigger.getElementsByTagName('ul').length > 0 ? trigger.getElementsByTagName('ul')[0] : null;

	//store link object
	trigger.link = trigger.getElementsByTagName('a')[0];

	//store whther this is a submenu or child menu
	//a submenu's parent node will have the navbar id
	trigger.issub = trigger.parentNode.id == navid;
	
	//whether this is a horizontal navbar
	trigger.ishoriz = ishoriz == 'horizontal';
	
	//menu opening events
	//onfocus doesn't bubble in ie, but its proprietary 'onactivate' event does
	//which works in win/ie5.5+
	this.openers = { 'm' : 'onmouseover', 'k' : (isie ? 'onactivate' : 'onfocus') };

	//bind menu openers
	for(var i in this.openers)
	{
		trigger[this.openers[i]] = function(e)
		{
			//set rollover persistence classname -- we have to check for an existing value first
			//because some opera builds don't allow the class attribute to have a leading space
			//don't do persistent rollovers for konqueror, because they stick in kde <= 3.0.4
			if(!iskde) { trigger.link.className += (trigger.link.className == '' ? '' : ' ') + 'rollover'; }

			//if trigger has a menu
			if(trigger.menu != null)
			{
				//show the menu by positioning it back on the screen
				//we can use the same positions as in pure CSS for most browsers ['css']
				//but we have to compute the positions for ie ['compute'] 
				//because it uses position:relative on <a> 
				//whereas the others have position:relative on <li>
				//we also need to use those values for old safari builds ['compute']
				//because the regular positioning doesn't work 
				
				//if this is a horizontal navbar
				//set the left position to auto [css] or compute a position [compute]
				if(trigger.ishoriz) { trigger.menu.style.left = (isie || isoldsaf) ? trigger.offsetLeft + 'px' : 'auto'; }
				
				//if this is a horizontal navbar and a first-level submenu 
				//set the top position to auto [css] or compute a position [compute]
				//otherwise set it to 0 [css] or compute a position [compute]
				trigger.menu.style.top = (trigger.ishoriz && trigger.issub) ? (isie || (trigger.ishoriz && isoldsaf)) ? trigger.link.offsetHeight + 'px' : 'auto' : (isie || (trigger.ishoriz && isoldsaf)) ? trigger.offsetTop + 'px' : '0';
			}
		};
	}


	//menu closing events
	//'ondeactivate' is the equivalent blur handler for 'onactivate'
	this.closers = { 'm' : 'onmouseout', 'k' : (isie ? 'ondeactivate' : 'onblur') };

	//bind menu closers
	for(i in this.closers)
	{
		trigger[this.closers[i]] = function(e)
		{
			//store event-related-target property
			this.related = (!e) ? window.event.toElement : e.relatedTarget;

			//if event came from outside current trigger branch
			if(!this.contains(this.related))
			{
				//reset rollover persistence classname; not for konqueror
				if(!iskde) { trigger.link.className = trigger.link.className.replace(/[ ]?rollover/g, ''); }
				
				//if trigger has a menu
				if(trigger.menu != null)
				{
					//hide menu using left for a horizontal menu, or top for a vertical
					trigger.menu.style[(trigger.ishoriz ? 'left' : 'top')] = trigger.ishoriz ? '-10000px' : '-100em';
				}
			}
		};
	}


	//contains method by jkd -- http://www.jasonkarldavis.com/
	//not necessary for ie because we're re-creating in ie-proprietary method
	//and it would throw errors in mac/ie5 anyway
	//not actually necessary for opera 7 either, because it's already implemented
	//but it won't do any harm, so spoofing doesn't matter
	if(!isie)
	{
		trigger.contains = function(node)
		{
			if (node == null) { return false; }
			if (node == this) { return true; }
			else { return this.contains(node.parentNode); }
		};
	}
}

