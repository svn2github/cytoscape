'''
Config.py

A class for reading simple key=value configuration file strings

'''

#Python imports
import os

class Config:
    '''
    This class implements a wrapper around configuration files. It attempts to
    read two files from the directory specified by the HOME environment variable.
    If home is not defined, then the instantiation of this object fails.
    During initialization, it reads .tga.conf and .my.cnf and loads all key/
    value pairs from these files into a dictionary called dict_. Keys in the
    .lssnp.conf will take precedence over identical keys in .hg.conf.
    '''
    
    #The configuration attribute
    dict_ = {}
    
    def __init__(self):
        '''
        Initialize the instance by reading in the configuration from the user's
        home directory. This class uses the values in .hg.conf first, followed
        by any values that exist in .lssnp.conf. The latter will override the 
        former if both files define the same key.
        '''
        #Get a path to the configuration files
        configPath = self._getConfigPath()
        self._loadConfigs(configPath+"chasm_classifiers.conf")

    
    def get(self, key):
        '''
        Retrieves a value from the configuration dictionary
        '''
        return self.dict_.get(key)


    def getWDefault(self, key, default):
        '''
        Retrieves a value from the configuration dictionary, returning a default
        value if the key is not found.      
        '''
        return self.dict_.get(key, default)
    
    
    def _loadConfigs(self, path):
        '''
        This function loads into the config_ dictionary all of the key/value
        pairs found in the specified file.        
        '''
        try:
            #Open the configuration file
            f = open(path, 'r')
            
            try:
                #For each line in the file, see if we have an '=' and parse a
                # key/value pair into the config_ dictionary
                for line in f:
                    if line.find('=') >= 0:
                        key, value = line.split('=', 2)
                        self.dict_[key] = value.strip()
            finally:
                #Close the file
                f.close()
                    
        except IOError:
            msg = "Config:_loadConfigs - IOError: Unable to open configuration file " + path
            raise Exception(msg)


    def _getConfigPath(self):
        '''
        Retrieves the configuration path from the environment. This function 
        will PREFER the value of the HOME variable, if defined. If this variable
        is not defined, it will look for the USERNAME and possibly USERDOMAIN
        variables and create a /home/USERNAME.USERDOMAIN path. If the CYGWIN
        variable is defined, then we will assume that any home directory is
        within the CYGWIN root directory. We attempt to locate this in C:\cygwin.       
        '''
        
        # Reads from the source directory of the code
        try:
            home = os.path.join(os.environ['CHASMDIR'])
            return home + os.sep
        except KeyError:
            msg = "Error: Please set $CHASMDIR environmental variable to the location of the CHASM installation"
            raise Exception(msg)

    
if __name__ == "__main__":
    # Quick test
    config = Config()
    print config.get("dataDir")

