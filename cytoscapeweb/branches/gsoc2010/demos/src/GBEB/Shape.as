package GBEB
{
	import flare.vis.data.EdgeSprite;

	public class Shape
	{
		public var gridSize:int;
		public var storedGrids:Array;
		public var gridIndex:Array;
		public var storedDataNodes:Array;
		public var storedDataEdges:Array;
		public var direction:Number = -1; //stores the main direction of edges that are inside the Shape. -1 if there is no main distance
		public var stronglyClustered:Boolean; //flag to indicate if the edges are strongly clustered within this Shape
		
		
		private const bandwidth:int = 3; //bandwidth of KDE
		private const majorityPercentage:Number = 0.5; // refers to the total weight percentage a particular group has to have before it can be considered as the main direction
		
		private var sum:Number = 0; 
		//private var numEdges:int = storedDataEdges.length;
		private var angleArray:Array = new Array(180/bandwidth + 1); //Each array block has a bandwidth of 3 degrees
		private var angleGroupWeightArray:Array = new Array(180/bandwidth + 1);
		private var majorityWeight:Number;
		private var angleArrayDirectionIdx:int = -1;
		
		//add getters and setters
		
		public function Shape():void
		{
			gridIndex = new Array();
			storedDataEdges = new Array();
			storedDataNodes = new Array();
			storedGrids = new Array();		
		}
		
		
		// Returns the general direction of the edges in this particular shape. x-axis = 0 degrees. Increases Counterclockwise.  
		// I am using a modified version of kernel density estimator with bandwidth = 3 degrees
		// This function ought to be called whenever the shape is merged 
		// TODO: separate KDE as another function
		public function computeDirection():Number
		{	//initialising all the angles in the array to zero. 
			for (var i:int = 0; i < angleArray.length ; i++)
			{
				var j:int = 0; //setting type for array. 
				angleArray[i] = j;
				angleGroupWeightArray[i] = j;
			}

			for each(var edge:EdgeSprite in storedDataEdges)
			{
				//getting the gradient of the line and expressing as a fraction of Pi. 
				var angle:int = getPolarCoor180(edge);
				
				//trace("Shape: CalculateDirection: " + edge.source.data["name"], edge.target.data["name"] + " has an angle of " + angle);
				
				var boxNo:int = Math.floor(angle / bandwidth);
				
				// The if statements are used to handle special cases where angle is close to zero or PI
				// The distributed weight to each box is determined by me using a approximately normal distribution of probability density. 
				// additional notes: Guassian Distribution can be used to generalise. 
				angleArray[((boxNo - 2 < 0) ? angleArray.length - 2 : boxNo - 2)] += 15;
				angleArray[((boxNo - 1 < 0) ? angleArray.length - 1 : boxNo - 1)] += 20;
				angleArray[boxNo] += 30;
				angleArray[((boxNo + 1 > angleArray.length - 1) ? 0 : boxNo + 1)] += 20;
				angleArray[((boxNo + 2 > angleArray.length - 1) ? 1 : boxNo +2)] += 15;	
			}
			
			for (var i:int = 0; i < angleArray.length; i++)
			{
				//trace("Shape " + gridIndex[0] + " : angleArray: " + i, angleArray[i]);
			}
			
			 majorityWeight = storedDataEdges.length * 100 * majorityPercentage; //for a directon to be the majority direction. It's group culmulative must be larger than this.  
			 
			//adds up the density for each angle, across a range of 15 degrees.  
			for (var i:int = 2; i < angleGroupWeightArray.length + 2; i++)
			{
				for( var j:int = -2; j <= 2; j++)
				{	
					angleGroupWeightArray[i % angleGroupWeightArray.length] += angleArray[(i+j) % angleArray.length ];
					
					if(angleGroupWeightArray[i % angleGroupWeightArray.length] > majorityWeight)
					{
						majorityWeight = angleGroupWeightArray[i % angleGroupWeightArray.length];
						angleArrayDirectionIdx = i % angleGroupWeightArray.length;
						stronglyClustered = true;
					}
				}		
			}
			
			if(stronglyClustered)
			{
				direction = angleArrayDirectionIdx * bandwidth; 
				trace("Shape " + gridIndex[0] + " :Direction " + direction); // should shift by 1.5 to get middle of bandwidth, but I dont think this level of accuracy is necessary
				return direction; 
			} 
				
			return -1;
		}
		
		//return the polarCoor of an Edge Sprite with a max diff of 180 degrees
		private function getPolarCoor180(e:EdgeSprite):Number
		{ 
			var angle:Number = Math.atan2 ((e.y2 - e.y1), (e.x2 - e.x1) );
			angle = Math.round(angle / Math.PI * 180); //working in degrees	
			angle = (angle < 0 ? 0 - angle : 180 - angle);
			//trace("Shape: getPolar: " + e.source.data["name"] + " to " + e.target.data["name"] + " | angle = " + angle);
			return angle; 
		}
		
		//return the polarCoor of an Edge Sprite with a max diff of 90 degrees
		private function getPolarCoor90(e:EdgeSprite):Number
		{ 
			var angle:Number = Math.atan2 ((e.y2 - e.y1), (e.x2 - e.x1) );
			angle = Math.round(angle / Math.PI * 180); //working in degrees
			angle = ( angle < 0 ? angle + 180 : angle); // inverts the direction for -ve regions
			//angle = ( angle > 90 ? 180 - angle : angle); // gets the acute angle
			return angle;
		}
	}
}