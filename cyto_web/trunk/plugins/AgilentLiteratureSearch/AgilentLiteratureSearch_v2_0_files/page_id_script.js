<!--

  var businessCode = "";
  var productLine ="";
  var breadcrumbHier = "";
  var contentID ="";
  var locale = "";
  var pageTitle="";
  var pageURL = window.document.URL.toString(); 
  var rawString="";

  if (document.all){
    MetaTagList=document.all.tags("meta");
  }
  else if (document.documentElement){
    MetaTagList=document.getElementsByTagName("meta"); 
  }

  for (i = 0; i < MetaTagList.length; i++) {
    if ( MetaTagList[i].name ) {
      if ( MetaTagList[i].name == 'DC.Publisher') {
        businessCode = MetaTagList[i].content
      }
	  if(MetaTagList[i].name == 'ENPUT_ID'){
	    contentID=MetaTagList[i].content
	  }
	  if(MetaTagList[i].name == 'Agilent.ProductLine'){
	    productLine =MetaTagList[i].content
	  }
	  if(MetaTagList[i].name == 'Agilent.Hierarchy'){
		breadcrumbHier = MetaTagList[i].content
	  }
	  }
      if (MetaTagList[i].httpEquiv) {

		 if(MetaTagList[i].httpEquiv == (MetaName = "Content-Language")){						
			locale = MetaTagList[i].content;			
		  }
      };
}

/*if((locale == null)||(locale==""))
	{
		for (i = 0; i < MetaTagList.length; i++) 
		{	
			if (MetaTagList[i].getAttributeNode) 
			{
				if(MetaTagList[i].getAttributeNode('http-equiv') == "Content-Language")
				{			
					MetaName = 'Content-Language';
					locale = MetaTagList[i].content;
				}
			}
		}
	}*/
/*
Function : getPageName
return   : pageName 
*/

function getPageName(){
	var pageName = "";	
	if(pageURL!="" && pageURL!=null){
	var count = pageURL.indexOf('?');
	if(count > -1)
	{
		count1 = pageURL.substring(0, count);
		count2 = pageURL.lastIndexOf('/');
		count3 = pageURL.lastIndexOf('.')
		pageName = pageURL.substring(count2+1, count3);
	}
	else{
		count = pageURL.lastIndexOf('/')
		count1 = pageURL.lastIndexOf('.')	
		pageName = pageURL.substring(count+1, count1);
	}
}
	return pageName.toUpperCase();
} //end

/*
This function returns the page for the omniture variable
Name : getOmniPageName();
return : OmniPageName
*/

function getOmniPageName(inPageName){
	//alert("Hellooooooo");
	pageTitle = document.title;	
	var OmniPageName =inPageName;
	if(OmniPageName!=null){					
		if(OmniPageName.length>49)
			OmniPageName = OmniPageName.substring(0,49);
		return OmniPageName;
	}
	else{
		pageName = getPageName();
		if (pageTitle!=null)
		{
			var countPipe = pageTitle.indexOf('|');	
			if(countPipe>-1)
			  pageTitle = pageTitle.substring(countPipe+1);

			if(pageTitle.length>49)
			  pageTitle = pageTitle.substring(0,49);

			OmniPageName = pageTitle+'|'+pageName;
			
		}		
	}
	return OmniPageName;
}

/*
This function returns the business unit code of the content
Name : getChannel();
return : businessUnitCode
*/
function getPageChannel(){
	var channelName = "";
	if(businessCode!=null)
		channelName = businessCode.substring(0,3);
	else
		channelName="";
	return channelName;

}

/*
This function returns the CountryCode of the content
Name : getCountryCode();
return : CountryCode
*/
function getCountryCode(){
	var countCc ="";
	var countyCode ="";
		if(locale!=null){
			countCc = locale.lastIndexOf('-');
			countyCode=locale.substring(countCc+1);
		}else{
			countyCode="";
		}

	return countyCode;
}

/*
This function returns the languageCode of the content
Name : getLanguageCode();
return : languageCode
*/
function getLanguageCode(){

	var countLc ="";
	var langCode="";
	if(locale!=null){
		countLc = locale.indexOf('-');
		langCode=locale.substring(0,countLc);
	}else{
		langCode="";
	}
	return langCode;
		
}

/*
This function returns the hiearchyValue of the content
Name : getBreadcrumbValue();
return : hiearchyValue
*/
function getBreadcrumbValue(){
 return breadcrumbHier;
}

/*
This function returns the Product Line meta tag value of the content
Name : getPLValue();
return : hiearchyValue
*/
function getPLValue(){
 return productLine;
}

/*
This function returns the CMP value of the URL
Name : getCMPValue();
return : campiagnID
*/
function getCMPValue(){

var qsReg = new RegExp("[?][^#]*","i");
var getCMPValue = "";
if(pageURL!="" && pageURL!=null){
var qsMatch = pageURL.match(qsReg);         
    qsMatch = new String(qsMatch);
    qsMatch = qsMatch.substr(1, qsMatch.length -1);        
    rawString = qsMatch;
	var rootArr = rawString.split("&");
	for(i=0;i<rootArr.length;i++){
            var tempArr =  rootArr[i].split("=");
            if(tempArr.length ==2){
                tempArr[0] = unescape(tempArr[0]);
				if (tempArr[0].toLowerCase()=='cmpid')
				{
				   tempArr[1] = unescape(tempArr[1]);				   
				   break;
				}                      
            }
        }
	getCMPValue = getOmniPageName()+'|'+tempArr[1];
}
	return getCMPValue;
}

//to add ger Value
function qsValue(key, value){
        this.key = key;
        this.value=value;    
    }
	
// to add items to the array
    function _add(obj){
        this.objects[this.objects.length] = obj;
    }

/*
This function returns the complete list 
of omniture values for templates
*/
function getOmnitureValues()
{

	var stringBuffer = new StringBuffer();
/*	s_pageName
	s_channel
	s_prop13
	s_prop15
	s_prop16
	s_prop21
	s_prop12
	s_hier1
	*/

	/*strMainBuffer.append('var s_pageName=');
	strMainBuffer.append("TestPageName");
	strMainBuffer.append('\n');

	strMainBuffer.append('var s_channel=');
	strMainBuffer.append("Channel");
	strMainBuffer.append('\n');

	strMainBuffer.append('var s_prop13=');
	strMainBuffer.append("TestPageName");
	strMainBuffer.append('\n');

	strMainBuffer.append('var s_prop15=');
	strMainBuffer.append("TestPageName");
	strMainBuffer.append('\n');

	strMainBuffer.append('var s_prop15=');
	strMainBuffer.append("TestPageName");
	strMainBuffer.append('\n');
*/


}

/*
var currentURL = document.URL;
if (currentURL.indexOf("/sec/") == -1) //sec is not in the url
{  
    var cookies     = document.cookie;
    var strRoleKey  = cookies.indexOf("role_agilent_com=");
    var strUsrKey   = cookies.indexOf("usr_agilent_com=");
    var strExpiredRoleKey = cookies.indexOf("role_agilent_com=anakincookieookie");
    if((strRoleKey != -1 && strUsrKey != -1) && strExpiredRoleKey == -1) //check to see if user is logged in
    {
        var strRE       = /\/agilent\//gi;
        var secureURL   = currentURL.replace(strRE,"/sec/");		
 
 		if ( secureURL.indexOf("/sec/") != -1 ) //sec is in the updated url			
			location.replace(secureURL);   		
    } 	
}
*/

-->
