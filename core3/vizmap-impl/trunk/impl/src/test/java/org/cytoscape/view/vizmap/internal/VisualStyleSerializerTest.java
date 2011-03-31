package org.cytoscape.view.vizmap.internal;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.vizmap.AbstractVisualStyleSerializerTest;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.internal.converters.CalculatorConverterFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class VisualStyleSerializerTest extends AbstractVisualStyleSerializerTest {

    Map<VisualStyle, String> styleNames;
    Map<VisualStyle, Map<VisualProperty<?>, ?>> styleProperties;

    @Before
    public void setUp() throws Exception {
        styleNames = new Hashtable<VisualStyle, String>();
        styleProperties = new Hashtable<VisualStyle, Map<VisualProperty<?>, ? extends Object>>();

        final VisualMappingManager visualMappingManager = mock(VisualMappingManager.class);
        final VisualMappingFunctionFactory discreteMappingFactory = mock(VisualMappingFunctionFactory.class);
        final VisualMappingFunctionFactory continuousMappingFactory = mock(VisualMappingFunctionFactory.class);
        final VisualMappingFunctionFactory passthroughMappingFactory = mock(VisualMappingFunctionFactory.class);
        final VisualStyle dummyDefaultStyle = createDefaultStyle();

        final VisualStyleFactory visualStyleFactory = mock(VisualStyleFactory.class);

        when(visualMappingManager.getDefaultVisualStyle()).thenReturn(dummyDefaultStyle);
        when(visualStyleFactory.getInstance(dummyDefaultStyle)).thenAnswer(new Answer<VisualStyle>() {

            public VisualStyle answer(InvocationOnMock invocation) throws Throwable {
                return createDefaultStyle();
            }
        });

        final RenderingEngineManager renderingEngineManager = mock(RenderingEngineManager.class);
        NullVisualProperty twoDRoot = new NullVisualProperty("TWO_D_ROOT", "2D Root Visual Property");
        when(renderingEngineManager.getDefaultVisualLexicon()).thenReturn(new MinimalVisualLexicon(twoDRoot));

        final CalculatorConverterFactory calcConvFactory = new CalculatorConverterFactory(discreteMappingFactory,
                                                                                          continuousMappingFactory,
                                                                                          passthroughMappingFactory);

        serializer = new VisualStyleSerializerImpl(visualStyleFactory, visualMappingManager, renderingEngineManager,
                                                   calcConvFactory);
    }

    @Test
    public void tesCreateVisualStyles() throws Exception {
        Properties props = new Properties();
        // Style A:
        props.setProperty("globalAppearanceCalculator.Style A.defaultBackgroundColor", "255,255,255");
        props.setProperty("edgeAppearanceCalculator.Style A.defaultEdgeLineWidth", "2.0");
        // Style B:
        props.setProperty("globalAppearanceCalculator.Style B.defaultBackgroundColor", "0,255,0");
        // Should IGNORE this:
        props.setProperty("nodeLabelCalculator.Style C-Node Label-Passthrough Mapper.mapping.controller", "ID");

        // TEST:
        Collection<VisualStyle> styles = serializer.createVisualStyles(props);

        assertEquals(2, styles.size());

        for (VisualStyle vs : styles) {
            String title = vs.getTitle();
            assertTrue(title.equals(styleNames.get(vs)));

            if (title.equals("Style A")) {
                //                assertEquals(new Color(255, 255, 255), vs.getDefaultValue(TwoDVisualLexicon.NETWORK_BACKGROUND_PAINT));
                //                assertEquals(new Double(2), vs.getDefaultValue(TwoDVisualLexicon.EDGE_WIDTH));
            } else if (title.equals("Style B")) {
                //                assertEquals(new Color(0, 255, 0), vs.getDefaultValue(TwoDVisualLexicon.NETWORK_BACKGROUND_PAINT));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private VisualStyle createDefaultStyle() {
        final VisualStyle vs = mock(VisualStyle.class);

        // stub get/setTitle
        doAnswer(new Answer<Void>() {

            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                styleNames.put(vs, (String) args[0]);
                return null;
            }
        }).when(vs).setTitle(anyString());

        when(vs.getTitle()).thenAnswer(new Answer<String>() {

            public String answer(InvocationOnMock invocation) throws Throwable {
                return styleNames.get(vs);
            }
        });

        // stub get/set default values
        doAnswer(new Answer<Void>() {

            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                VisualProperty<?> vp = (VisualProperty) args[0];
                Object val = args[1];

                Map vpMap = styleProperties.get(vs);
                if (vpMap == null) {
                    vpMap = new Hashtable();
                    styleProperties.put(vs, vpMap);
                }

                vpMap.put(vp, val);

                return null;
            }
        }).when(vs).setDefaultValue(any(VisualProperty.class), anyObject());

        when(vs.getDefaultValue(any(VisualProperty.class))).thenAnswer(new Answer<Object>() {

            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object val = null;
                Object[] args = invocation.getArguments();
                VisualProperty<?> vp = (VisualProperty) args[0];

                if (vp != null) {
                    Map vpMap = styleProperties.get(vs);
                    if (vpMap != null) val = vpMap.get(vp);
                }

                return val;
            }
        });

        return vs;
    }

    //    @Test
    //    public void convertOldProperty() {
    //        Map<String, String> m;
    //        Properties p = new Properties();
    //        
    //        // Default properties ------------
    //        // Edge - LineType
    //        m = VisualStyleSerializerImpl.updateOldProperty("edgeAppearanceCalculator.a.defaultEdgeLineType", "LINE_1", p);
    //        assertEquals("SOLID", m.get("edgeAppearanceCalculator.a.defaultEdgeLineStyle"));
    //        assertEquals("1", m.get("edgeAppearanceCalculator.a.defaultEdgeLineWidth"));
    //        
    //        m = VisualStyleSerializerImpl.updateOldProperty("edgeAppearanceCalculator.a.defaultEdgeLineType", "DASHED_3", p);
    //        assertEquals("EQUAL_DASH", m.get("edgeAppearanceCalculator.a.defaultEdgeLineStyle"));
    //        assertEquals("3", m.get("edgeAppearanceCalculator.a.defaultEdgeLineWidth"));
    //        
    //        // Node - LineType
    //        m = VisualStyleSerializerImpl.updateOldProperty("nodeAppearanceCalculator.a.defaultNodeLineType", "LINE_7", p);
    //        assertEquals("SOLID", m.get("nodeAppearanceCalculator.a.defaultNodeLineStyle"));
    //        assertEquals("7", m.get("nodeAppearanceCalculator.a.defaultNodeLineWidth"));
    //        
    //        m = VisualStyleSerializerImpl.updateOldProperty("nodeAppearanceCalculator.a.defaultNodeLineType", "DASHED_5", p);
    //        assertEquals("EQUAL_DASH", m.get("nodeAppearanceCalculator.a.defaultNodeLineStyle"));
    //        assertEquals("5", m.get("nodeAppearanceCalculator.a.defaultNodeLineWidth"));
    //        
    //        // Edge - Arrow
    //        m = VisualStyleSerializerImpl.updateOldProperty("edgeAppearanceCalculator.a.defaultEdgeSourceArrow", "NONE", p);
    //        assertEquals("NONE", m.get("edgeAppearanceCalculator.a.defaultEdgeSourceArrowShape"));
    //        assertNull(m.get("edgeAppearanceCalculator.a.defaultEdgeTargetArrowColor"));
    //        
    //        m = VisualStyleSerializerImpl.updateOldProperty("edgeAppearanceCalculator.a.defaultEdgeTargetArrow", "COLOR_DIAMOND", p);
    //        assertEquals("DIAMOND", m.get("edgeAppearanceCalculator.a.defaultEdgeTargetArrowShape"));
    //        assertNull(m.get("edgeAppearanceCalculator.a.defaultEdgeTargetArrowColor"));
    //        
    //        m = VisualStyleSerializerImpl.updateOldProperty("edgeAppearanceCalculator.a.defaultEdgeSourceArrow", "WHITE_DELTA", p);
    //        assertEquals("DELTA", m.get("edgeAppearanceCalculator.a.defaultEdgeSourceArrowShape"));
    //        assertEquals("255,255,255", m.get("edgeAppearanceCalculator.a.defaultEdgeSourceArrowColor"));
    //        
    //        m = VisualStyleSerializerImpl.updateOldProperty("edgeAppearanceCalculator.a.defaultEdgeTargetArrow", "BLACK_CIRCLE", p);
    //        assertEquals("CIRCLE", m.get("edgeAppearanceCalculator.a.defaultEdgeTargetArrowShape"));
    //        assertEquals("0,0,0", m.get("edgeAppearanceCalculator.a.defaultEdgeTargetArrowColor"));
    //        
    //        // No changes required
    //        m = VisualStyleSerializerImpl.updateOldProperty("nodeAppearanceCalculator.a.defaultNodeShape", "rect", p);
    //        assertEquals("rect", m.get("nodeAppearanceCalculator.a.defaultNodeShape"));
    //        
    //        m = VisualStyleSerializerImpl.updateOldProperty("nodeUniformSizeCalculator.Node Size-Continuous Mapper.mapping.boundaryvalues", "1", p);
    //        assertEquals("1", m.get("nodeUniformSizeCalculator.Node Size-Continuous Mapper.mapping.boundaryvalues"));
    //    }

    @Test
    public void testGetStyleName() {
        assertEquals("My style", VisualStyleSerializerImpl
                .getStyleName("globalAppearanceCalculator.My style.defaultBackgroundColor"));
        assertEquals("My style 2", VisualStyleSerializerImpl
                .getStyleName("nodeAppearanceCalculator.My style 2.defaultNodeBorderColor"));
        assertEquals("default", VisualStyleSerializerImpl
                .getStyleName("edgeAppearanceCalculator.default.defaultEdgeToolTip"));
        assertEquals("My style", VisualStyleSerializerImpl
                .getStyleName("nodeAppearanceCalculator.My style.nodeLabelCalculator"));
        assertEquals("default", VisualStyleSerializerImpl
                .getStyleName("nodeAppearanceCalculator.default.defaultNodeCustomGraphics1"));
        assertEquals("My style", VisualStyleSerializerImpl
                .getStyleName("nodeAppearanceCalculator.My style.nodeSizeLocked"));
        assertEquals("My style", VisualStyleSerializerImpl
                .getStyleName("nodeAppearanceCalculator.My style.defaultNodeShowNestedNetworks"));
        assertEquals("My style", VisualStyleSerializerImpl
                .getStyleName("nodeAppearanceCalculator.My style.nodeLabelColorFromNodeColor"));
        assertEquals("My style", VisualStyleSerializerImpl
                .getStyleName("nodeAppearanceCalculator.My style.nodeCustomGraphicsSizeSync"));
        assertEquals("My style", VisualStyleSerializerImpl
                .getStyleName("edgeAppearanceCalculator.My style.arrowColorMatchesEdge"));

        assertNull(VisualStyleSerializerImpl
                .getStyleName("nodeBorderColorCalculator.My style-Node Border Color-Discrete Mapper.mapping.map.A03"));
        assertNull(VisualStyleSerializerImpl
                .getStyleName("edgeColorCalculator.default-Edge Color-Continuous Mapper.mapping.boundaryvalues=2"));
    }

    @Test
    public void testIsDefaultProperty() {
        assertTrue(VisualStyleSerializerImpl
                .isDefaultProperty("globalAppearanceCalculator.My style.defaultBackgroundColor"));
        assertTrue(VisualStyleSerializerImpl
                .isDefaultProperty("globalAppearanceCalculator.My style.defaultNodeSelectionColor"));
        assertTrue(VisualStyleSerializerImpl
                .isDefaultProperty("globalAppearanceCalculator.My style.defaultEdgeReverseSelectionColor"));
        assertTrue(VisualStyleSerializerImpl
                .isDefaultProperty("nodeAppearanceCalculator.My style.defaultNodeBorderColor"));
        assertTrue(VisualStyleSerializerImpl
                .isDefaultProperty("nodeAppearanceCalculator.My style.defaultNodeBorderOpacity"));
        assertTrue(VisualStyleSerializerImpl.isDefaultProperty("nodeAppearanceCalculator.default.defaultNodeFont"));
        assertTrue(VisualStyleSerializerImpl.isDefaultProperty("nodeAppearanceCalculator.My style.defaultNodeHight"));
        assertTrue(VisualStyleSerializerImpl.isDefaultProperty("nodeAppearanceCalculator.My style.defaultNodeLabel"));
        assertTrue(VisualStyleSerializerImpl
                .isDefaultProperty("nodeAppearanceCalculator.My style.defaultNodeLineStyle"));
        assertTrue(VisualStyleSerializerImpl
                .isDefaultProperty("nodeAppearanceCalculator.My style.defaultNodeCustomGraphics1"));
        assertTrue(VisualStyleSerializerImpl
                .isDefaultProperty("nodeAppearanceCalculator.My style.defaultNodeCustomGraphicsPosition8"));
        assertTrue(VisualStyleSerializerImpl.isDefaultProperty("edgeAppearanceCalculator.My style.defaultEdgeToolTip"));
        assertTrue(VisualStyleSerializerImpl
                .isDefaultProperty("edgeAppearanceCalculator.default.defaultEdgeTargetArrowColor"));

        assertFalse(VisualStyleSerializerImpl
                .isDefaultProperty("nodeAppearanceCalculator.My style.nodeLabelCalculator"));
        assertFalse(VisualStyleSerializerImpl.isDefaultProperty("nodeAppearanceCalculator.My style.nodeSizeLocked"));
        assertFalse(VisualStyleSerializerImpl
                .isDefaultProperty("nodeAppearanceCalculator.My style.defaultNodeShowNestedNetwork"));
        assertFalse(VisualStyleSerializerImpl.isDefaultProperty("edgeAppearanceCalculator.default.defaultNodeSize"));
        assertFalse(VisualStyleSerializerImpl.isDefaultProperty("nodeAppearanceCalculator.default.defaultEdgeColor"));
    }

    @Test
    public void testIsMappingFunction() {
        assertTrue(VisualStyleSerializerImpl.isMappingFunction("nodeAppearanceCalculator.My style.nodeLabelCalculator"));
        assertTrue(VisualStyleSerializerImpl
                .isMappingFunction("nodeAppearanceCalculator.My style.nodeBorderColorCalculator"));
        assertTrue(VisualStyleSerializerImpl.isMappingFunction("nodeAppearanceCalculator.My style.nodeCustomGraphics1"));
        assertTrue(VisualStyleSerializerImpl
                .isMappingFunction("nodeAppearanceCalculator.My style.nodeCustomGraphicsPosition4"));
        assertTrue(VisualStyleSerializerImpl.isMappingFunction("edgeAppearanceCalculator.default.edgeColorCalculator"));
        assertTrue(VisualStyleSerializerImpl
                .isMappingFunction("nodeAppearanceCalculator.galFiltered Style.nodeLabelColor"));

        assertFalse(VisualStyleSerializerImpl.isMappingFunction("nodeAppearanceCalculator.My style.nodeSizeLocked"));
        assertFalse(VisualStyleSerializerImpl
                .isMappingFunction("nodeAppearanceCalculator.My style.defaultNodeShowNestedNetwork"));
        assertFalse(VisualStyleSerializerImpl
                .isMappingFunction("nodeAppearanceCalculator.My style.nodeCustomGraphicsSizeSync"));
        assertFalse(VisualStyleSerializerImpl
                .isMappingFunction("nodeAppearanceCalculator.My style.nodeLabelColorFromNodeColor"));
        assertFalse(VisualStyleSerializerImpl
                .isMappingFunction("nodeAppearanceCalculator.My style.defaultNodeCustomGraphics1"));
        assertFalse(VisualStyleSerializerImpl
                .isMappingFunction("nodeAppearanceCalculator.My style.defaultNodeCustomGraphicsPosition2"));
        assertFalse(VisualStyleSerializerImpl
                .isMappingFunction("globalAppearanceCalculator.My style.defaultBackgroundColor"));
        assertFalse(VisualStyleSerializerImpl.isMappingFunction("edgeAppearanceCalculator.default.defaultEdgeColor"));
        assertFalse(VisualStyleSerializerImpl
                .isMappingFunction("nodeBorderColorCalculator.My Style-Node Border Color-Discrete Mapper.mapping.controller"));
    }

    @Test
    public void testIsDependency() {
        assertTrue(VisualStyleSerializerImpl.isDependency("nodeAppearanceCalculator.My style.nodeSizeLocked"));
        assertTrue(VisualStyleSerializerImpl
                .isDependency("nodeAppearanceCalculator.My style.defaultNodeShowNestedNetwork"));
        assertTrue(VisualStyleSerializerImpl
                .isDependency("nodeAppearanceCalculator.My style.nodeLabelColorFromNodeColor"));
        assertTrue(VisualStyleSerializerImpl
                .isDependency("nodeAppearanceCalculator.My style.nodeCustomGraphicsSizeSync"));
        assertTrue(VisualStyleSerializerImpl.isDependency("edgeAppearanceCalculator.My style.arrowColorMatchesEdge"));

        assertFalse(VisualStyleSerializerImpl
                .isDependency("globalAppearanceCalculator.My style.defaultBackgroundColor"));
        assertFalse(VisualStyleSerializerImpl.isDependency("nodeAppearanceCalculator.My style.nodeCustomGraphics1"));
        assertFalse(VisualStyleSerializerImpl
                .isDependency("nodeAppearanceCalculator.My style.nodeCustomGraphicsPosition4"));
        assertFalse(VisualStyleSerializerImpl
                .isDependency("nodeAppearanceCalculator.nodeCustomGraphicsSizeSync.defaultNodeCustomGraphicsPosition2"));
        assertFalse(VisualStyleSerializerImpl
                .isMappingFunction("nodeBorderColorCalculator.nodeCustomGraphicsSizeSync-Node Border Color-Discrete Mapper.mapping.controller"));
    }
}
