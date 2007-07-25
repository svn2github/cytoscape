import infovis.utils.IdManager;
import junit.framework.TestCase;

/*****************************************************************************
 * Copyright (C) 2004 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * See the file LICENCE.TXT for license information.                         *
 *****************************************************************************/

/**
 * Class IdManagerTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class IdManagerTest extends TestCase {
    public IdManagerTest(String name) {
        super(name);
    }
    
    public void testIdManager()  {
        IdManager mgr = new IdManager();
        
        assertEquals(0, mgr.newId());
        assertEquals(1, mgr.newId());
        assertEquals(2, mgr.newId());
        assertEquals(3, mgr.newId());
        mgr.free(3);
        assertEquals(3, mgr.newId());
        mgr.free(0);
        assertEquals(0, mgr.newId());
        mgr.free(2);
        assertEquals(0, mgr.getMinId());
        assertEquals(3, mgr.getMaxId());
        mgr.free(1);
        assertEquals(0, mgr.getMinId());
        assertEquals(3, mgr.getMaxId());
        mgr.free(0);
        assertEquals(3, mgr.getMinId());
        assertEquals(3, mgr.getMaxId());
        mgr.free(3);
        assertEquals(0, mgr.getMinId());
        assertEquals(-1, mgr.getMaxId());
        
        //mgr.clear();
        for (int i = 0; i < 100; i++) {
            mgr.newId();
        }
        assertEquals(0, freeNew(0, mgr));
        assertEquals(100, freeNew(100, mgr));
        assertEquals(50, freeNew(50, mgr));
        assertEquals(1, freeNew(1, mgr));
    }

    protected int freeNew(int id, IdManager mgr) {
        mgr.free(id);
        return mgr.newId();
    }
}
