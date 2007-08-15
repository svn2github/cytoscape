//GO1.1


///////////////////////////////////////
//
//  Generic onload by Brothercake
//  Necessary when multiple functions depend on the windows.onload or body.onload
//  http://www.brothercake.com/
//
///////////////////////////////////////



//onload function
function generic()
{
	loadheader();
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

// loading of top banner
function loadheader()
{
      var bimages = ["0-banner.jpg", "1-banner.jpg", "2-banner.jpg", "3-banner.jpg", "4-banner.jpg", "5-banner.jpg"];
      var rand=Math.floor(Math.random()*6);
      newImage = "url(images/banner/" + bimages[rand] + ")";
      document.getElementById('adambanner').style.backgroundImage = newImage;
}
