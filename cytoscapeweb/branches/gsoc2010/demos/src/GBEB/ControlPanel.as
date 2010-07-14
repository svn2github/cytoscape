package GBEB
{	
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.geom.Rectangle;
	import flash.text.TextField;
	import flash.text.TextFormat;
	import flash.text.TextFormatAlign;
	
	import flashx.textLayout.formats.TextAlign;


	public class ControlPanel extends Sprite
	{
		public static const Ctr_GBEB:String = "Ctr: GBEB";
		public static const Ctr_Graph:String = "Ctr: Graph";
		
		
		private var _GBEB:GBEB;
		
		//Components
		private var _CPContainer:Sprite = new Sprite();
		private var _swapBtn:Sprite = new Sprite();
		private var _redrawMeshBtn:Sprite = new Sprite();
		
		private var _rec:Rectangle = new Rectangle(545, 5, 450, 65); // initial coordinates and def of CPContainer
		private var _btnRec:Rectangle = new Rectangle(0, 0, 100, 30);
		private var _swapBtnLabel:TextField;
		private var _numBtn:int = 0; //tracks the number of Btns in this container
		private var _textFormat:TextFormat = new TextFormat();
		private var _swapVis:Function;
		
		public function ControlPanel(gbeb:GBEB, swapVis:Function) 
		{
			_GBEB = gbeb;
			_swapVis = swapVis;
			
			_textFormat.align = TextAlign.CENTER;
			
			initCPContainer();

			trace("ControlPanel: is added");
		}
		
				private function initCPContainer():void
				{
					var background:Sprite = new Sprite();
					
					background.graphics.beginFill(0xFAFAFA, 0.5);
					background.graphics.lineStyle(2, 0x000000);
					background.graphics.drawRect(_rec.x, _rec.y, _rec.width, _rec.height);
					background.graphics.endFill();
					
					_CPContainer.addChild(background);
					
					initSwapBtn();
					redrawMeshBtn();
					
					addChild(_CPContainer);
				}
				
				private function initSwapBtn():void
				{
					_swapBtnLabel = initBtn(_swapBtn, Ctr_Graph);	
	
					_swapBtn.addEventListener(MouseEvent.CLICK, function swapBtnClicked(e:MouseEvent):void {
						_swapBtnLabel.text = ( _swapBtnLabel.text == Ctr_GBEB ? Ctr_Graph : Ctr_GBEB   );
						_swapBtnLabel.setTextFormat(_textFormat);
						
						_swapVis();
					});
					
				}
				
				public function redrawMeshBtn():void
				{
					initBtn(_redrawMeshBtn, "Redraw Mesh");
				}
			
				private function initBtn(Btn:Sprite, label:String):TextField
				{
					var btnLabel:TextField = new TextField();
					var btnModeSprite:Sprite = new Sprite(); // Just for aesthetic purposes
					
					Btn.x = _rec.x + _numBtn * (_btnRec.width + 5);
					Btn.y = _rec.y + 5;
					
					Btn.graphics.beginFill(0x67C8FF, 1);
					Btn.graphics.lineStyle(1, 0x000000);
					Btn.graphics.drawRect( 0, 0, _btnRec.width, _btnRec.height);
					Btn.graphics.endFill();
					
					btnModeSprite.graphics.beginFill(0x000000, 0);
					btnModeSprite.graphics.drawRect(0, 0, _btnRec.width, _btnRec.height);
					btnModeSprite.graphics.endFill();
					btnModeSprite.buttonMode = true;
					
					
					btnLabel.text = label;
					btnLabel.height = _btnRec.height; btnLabel.width = _btnRec.width;
					btnLabel.y += 9; //padding		
					btnLabel.multiline = true;
					btnLabel.selectable = false;
					btnLabel.setTextFormat(_textFormat);

					_numBtn +=1;
					Btn.addChild(btnLabel);
					Btn.addChild(btnModeSprite);
					
					_CPContainer.addChild(Btn);
					
					return btnLabel;
				}
				
				
				
				
				private function testDraw():void
				{
					var a:Sprite = new Sprite();
					a.graphics.beginFill(0x42C0FB);
					a.graphics.drawCircle(200, 200, 100);
					a.graphics.endFill();
					
					addChild(a);
				}
				
				public function updateControlPanel(gbeb:GBEB = null, label:String = Ctr_Graph):void
				{
					if (gbeb != null) _GBEB = gbeb;
					_swapBtnLabel.setTextFormat(_textFormat);
					_swapBtnLabel.text = label;
				}
				
		
		
	} //end of class
}