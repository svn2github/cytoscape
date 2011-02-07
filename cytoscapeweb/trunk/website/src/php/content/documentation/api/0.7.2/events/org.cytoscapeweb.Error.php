<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.Error";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
				
            
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.Error";
				
				    $fn->description = "<p>This object represents an Error type, but is just an untyped object.</p>
<p>It is returned by <code>\"error\"</code> type events.</p>";
				
				    $fn->is_constructor = true;
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.EventType";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Event";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#addListener";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

				
						
                        $field = new param();
                        
                        
                            $field->type = "String";
                        
                        
                        $field->name = "id";
						$field->description = "The error id.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "String";
                        
                        
                        $field->name = "msg";
						$field->description = "The error message.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "String";
                        
                        
                        $field->name = "name";
						$field->description = "The error name.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "String";
                        
                        
                        $field->name = "stackTrace";
						$field->description = "The stack trace of the error.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>