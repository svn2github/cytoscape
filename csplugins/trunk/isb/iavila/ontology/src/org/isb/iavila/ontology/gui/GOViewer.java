package org.isb.iavila.ontology.gui;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*; 
import java.awt.Dimension;
import java.util.*;
import java.awt.GridLayout;

import org.isb.iavila.ontology.xmlrpc.*;
import org.isb.iavila.ontology.*;

/**
 * A JFrame that displays a JTree with DefaultMutableTreeNodes with OntologyTerms as the user objects.
 * @author iavila
 */
public class GOViewer extends JPanel{
    
    protected GOClient goClient;
    protected JTree tree;
    protected DefaultMutableTreeNode rootNode;
    
    
    /**
     * @param go_client the GOClient from which to obtain ontology information
     */
    public GOViewer (GOClient go_client){
        super(new GridLayout(1,0));
        this.goClient = go_client;   
        create();
    }
    
    /**
     * @return the GOClient this GOViewer uses to obtain information
     */
    public GOClient getGOClient (){
        return this.goClient;
    }
    
    /**
     * @return the root TreeNode of the tree
     */
    public TreeNode getRootNode (){
        return this.rootNode;
    }
    
    /**
     * Creates the viewer
     */
    protected void create (){
        
        Hashtable termToChildren = null;
        Hashtable termsToNames = null;
        Integer rootID = null;
        
        try{
            termToChildren = this.goClient.getTermsChildren();
            termsToNames = this.goClient.getTermsToNames();
            rootID = this.goClient.getRootTermID();
        }catch (Exception e){
            e.printStackTrace();
            JLabel errorLabel = new JLabel(e.getMessage());
            add(errorLabel);
            return;
        }//catch
        
        // The first term is the root:
        String rootName = (String)termsToNames.get(rootID.toString());
        this.rootNode = 
            new DefaultMutableTreeNode(new OntologyTerm(rootID.intValue(),rootName));
        createNodes(this.rootNode, termToChildren, termsToNames);
        this.tree = new JTree(this.rootNode);
        JScrollPane treeView = new JScrollPane(this.tree);
        treeView.setPreferredSize(new Dimension(100, 250));
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        // For now:
//        this.tree.addTreeSelectionListener(
//                
//                new TreeSelectionListener(){
//                    
//                    public void valueChanged(TreeSelectionEvent e){
//                        getSelectedTerms();
//                    } 
//                    
//                    
//                }//TreeSelectionListner
//                
//        );
        add(treeView);
    }
    
    /**
     * Recursively creates the ontology tree using DefaultMutableTreeNodes
     * @param rootID
     */
    protected void createNodes (DefaultMutableTreeNode root, 
            Hashtable termToChildren, Hashtable termsToNames){
        
        OntologyTerm rootTerm = (OntologyTerm)root.getUserObject();
        Vector children = (Vector)termToChildren.get(Integer.toString(rootTerm.getID()));
        if(children == null) return; // leaf
        Iterator it = children.iterator();
        while(it.hasNext()){
            String childID = (String)it.next();
            String childName = (String)termsToNames.get(childID);
            OntologyTerm childTerm = new OntologyTerm(Integer.parseInt(childID), childName);
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childTerm);
            root.add(childNode);
            createNodes(childNode, termToChildren, termsToNames);
        }
    }
    
    /**
     * @return an array of OntologyTerms that are currently selected
     */
    public OntologyTerm [] getSelectedTerms (){
        
        TreePath [] paths = this.tree.getSelectionPaths();
        
        if(paths.length == 0) return new OntologyTerm[0];
        
        ArrayList list = new ArrayList();
        for(int i = 0; i < paths.length; i++){
            TreeNode treeNode = (TreeNode)paths[i].getLastPathComponent();
            //System.out.println(treeNode);
            list.add( ( (DefaultMutableTreeNode)treeNode ).getUserObject());
        }//for i
        
        return (OntologyTerm [])list.toArray(new OntologyTerm[0]);
    }
   
}