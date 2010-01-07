<?php
    
    // simple structs for building the api
    // cls -> func -> param

    class param {
        public $name;
        public $description;
        public $type;
        public $default_value;
        public $optional = false;
        public $examples = array(); // list of strings
        public $depreciated;
        public $see = array(); // list of strings
    }
       
    class func {
        public $name;
        public $description;
        public $file;
        public $is_constructor = false;
        public $params = array(); // array(name => param)
        public $return_value; // param
        public $examples = array(); // array(list of strings)
        public $depreciated = false;
        public $exceptions = array(); // array(name => param)
        public $preconditions = array(); // list of strings
        public $see = array(); // list of strings
    }
    
    class cls {       
        public $name;
        public $description;
        public $funcs = array(); // array(name => func)
        public $constructor;
        public $events = array(); // array(name => func)
        public $fields = array(); // array(name => param)
        public $see = array(); // list of strings
    }
    
    class api {
        public $categories = array(); // array(name => category)
        public $cls_name_to_cat_name = array(); // array(cls_name => cat_name)
        public $version;
        public $date;
    }
    
    class category {
        public $name;
        public $files = array();
        public $real_class = false;
    }

?>