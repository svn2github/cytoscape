package org.genmapp.subgeneviewer.splice;

import org.genmapp.subgeneviewer.SubgeneNetworkViewMediator;
import org.genmapp.subgeneviewer.splice.view.SpliceViewMediatorImpl;

public interface SpliceViewMediator extends SubgeneNetworkViewMediator {

	public static SpliceViewMediator INSTANCE = new SpliceViewMediatorImpl();

}
