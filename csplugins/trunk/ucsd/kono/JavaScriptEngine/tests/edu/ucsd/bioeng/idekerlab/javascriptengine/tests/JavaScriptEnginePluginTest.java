package edu.ucsd.bioeng.idekerlab.javascriptengine.tests;

import java.io.FileReader;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.IOUtils;

import junit.framework.TestCase;

public class JavaScriptEnginePluginTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testJavaScriptEnginePlugin() throws Exception {
		 // スクリプトエンジン名
		String engineName = "javascript";
		// スクリプトファイル名
		String scriptFileName = "hello.js";

	

		// BSFマネージャを生成する
		BSFManager bsfmgr = new BSFManager();

		// 標準以外のスクリプトエンジンを利用する場合は登録をする(今回は groovyとruby )
		BSFManager.registerScriptingEngine("javascript", 
				"org.apache.bsf.engines.javascript.JavaScriptEngine", new String[] { "javascript" });
		

		// スクリプトのグローバル変数としてJavaオブジェクトを登録する
		bsfmgr.declareBean("value", "Java", String.class);
		
		System.out.println("============= Running Script =============");

		// エンジンを指定して，スクリプトを実行する
		Object obj = bsfmgr.eval( engineName, scriptFileName, 1, 1, 
		IOUtils.getStringFromReader(new FileReader(scriptFileName)));

	}

}
