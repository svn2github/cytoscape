/*** UNTESTED ***/

var supported = (document.getElementById || document.all || document.layers);

function getObj(name)
{
  if (document.getElementById)
  {
    this.obj = document.getElementById(name);
    this.style = document.getElementById(name).style;
  }
  else if (document.all)
  {
    this.obj = document.all[name];
    this.style = document.all[name].style;
  }
  else if (document.layers)
  {
    this.obj = document.layers[name];
    this.style = document.layers[name];
  }
}

function onload_action(n_bp, n_cc, n_mf)
{   
  if(!supported) return;
  CategoryVisibility_GroupToggle('bp','no-effect',n_bp);
  CategoryVisibility_GroupToggle('cc','no-effect',n_cc);
  CategoryVisibility_GroupToggle('mf','no-effect',n_mf);
  return false;
}

function CategoryVisibility_GroupToggle(name,effect_opt,num_results)
{
  if(!supported) return;
  var n_visible = 0;
  var blocks = document.getElementsByName(name);
  if(blocks.length > 0)
  {
    //alert('in the document.getElementsByName branch');
    var link_name = name+'_link';
    var links = document.getElementsByName(link_name);
    if(links.length != blocks.length){
      alert('links.length='+links.length+' blocks.length='+blocks.length);
    }
    var n = blocks.length;
    var m = n / 2;
    for(var i=0; i < n; i++)
    {
      var x = new getObj(blocks[i].id);
      if(x.style.display != 'none'){ n_visible++; }
    }
    for(var i = 0; i < n; i++)
    {
      var x = new getObj(blocks[i].id);
      if(n_visible == n)  { x.style.display = 'none'; rewrite(links[i].id,'hide'); continue; }
      if(n_visible == 0)  { x.style.display = ''; rewrite(links[i].id,'show'); continue; }
      if(n_visible == m || n_visible < m ){ x.style.display = ''; rewrite(links[i].id,'show'); continue; }
      if(n_visible > m)   { x.style.display = 'none'; rewrite(links[i].id,'hide'); continue; }
    }
  }
  else
  {
    //alert('in the alt branch\nname='+name+'\nnum_results='+num_results);
    var m = (num_results)/2;
    for(var i = 1; i <= num_results; i++){
      var ext = '';
      switch(name){
        case 'mf': ext = '_mol_func'; break;
        case 'bp': ext = '_bio_proc'; break;
        default:   ext = '_cel_comp'; break;
      }
      var id = 'menu_block'+ext+i;
      var x = new getObj(id);
      if(x.style.display != 'none'){ n_visible++; }
    }
    for(var i = 1; i <= num_results; i++){
      var ext = '';
      switch(name){
        case 'mf': ext = '_mol_func'; break;
        case 'bp': ext = '_bio_proc'; break;
        default:   ext = '_cel_comp'; break;
      }
      var block_id = 'menu_block'+ext+i;
      var link_id  = block_id+'_link';
      //alert('n_visible for '+name+' = '+n_visible+'. block_id='+block_id);
      var x = new getObj(block_id);
      if(n_visible == num_results)         { x.style.display = 'none'; rewrite(link_id,'hide'); continue; }
      if(n_visible == 0)                   { x.style.display = ''; rewrite(link_id,'show'); continue; }
      if((n_visible == m)||(n_visible < m)){ x.style.display = ''; rewrite(link_id,'show'); continue; }
      if(n_visible > m)                    { x.style.display = 'none'; rewrite(link_id,'hide'); continue; }
    }
  }
}

function CategoryVisibility_Toggle(name,block_id)
{
  if(!supported) return;
  var blocks = document.getElementsByName(name);
  var x = new getObj(block_id);
  var link_id = block_id+'_link';
  var y = new getObj(link_id);
  if(blocks.length > 0)
  {
    if(x.style.display != 'none') { new Effect.Fade(block_id,{duration:0.5});   rewrite(link_id,'hide'); }
    else                          { new Effect.Appear(block_id,{duration:0.5}); rewrite(link_id,'show'); }
  }
  else{
    if(x.style.display != 'none') { x.style.display = 'none'; rewrite(link_id,'hide'); }
    else                          { x.style.display = ''; rewrite(link_id,'show'); }
  }
}

var tag_regExp = /<\/?[^>]+>/gi; //this regex matches tags
var max_re = /\+&nbsp;&nbsp;/;
var min_re = /\-&nbsp;&nbsp;/;
var max_str = "+&nbsp;&nbsp;";
var min_str = "-&nbsp;&nbsp;";
function rewrite(link_id,opt)
{
  var xContent = document.getElementById(link_id).innerHTML;
  if(opt == 'show'){ if(max_re.test(xContent)){ document.getElementById(link_id).innerHTML = xContent.replace(max_re, min_str); } }
  if(opt == 'hide'){ if(min_re.test(xContent)){ document.getElementById(link_id).innerHTML = xContent.replace(min_re, max_str); } }
}   


/*** TESTED ***/

function SelectAll(val)
{
  for(var i=0;i<document.search.elements.length;i++)
  {
if(document.search.elements[i].type == "checkbox")
{
  document.search.elements[i].checked = val;
}
  }
}

function LoadExampleQuery(val1,val2)
{
  document.search.search_query.value = val1 + ' ' + '"'+val2+'"';
} 
