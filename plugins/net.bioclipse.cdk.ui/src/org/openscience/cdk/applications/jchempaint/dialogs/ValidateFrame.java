/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-07-06 15:38:14 +0200 (Thu, 06 Jul 2006) $
 * $Revision: 1172 $
 *
 * Copyright (C) 2003-2005  The JChemPaint project
 *
 * Contact: jchempaint-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications.jchempaint.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.applications.jchempaint.action.ValidateAction;
import org.openscience.cdk.applications.swing.FieldTablePanel;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.validate.ProblemMarker;
import org.openscience.cdk.validate.ValidationReport;
import org.openscience.cdk.validate.ValidationTest;

/**
 * Frame to allows editing of dictionary references of 
 * ChemObjects.
 *
 * @cdk.module jchempaint
 */
public class ValidateFrame extends JFrame  {
    
    private Renderer2DModel rendererModel;
    private JPanel errorTreePanel;
    private JPanel warningTreePanel;
    private JPanel cdkErrorTreePanel;
    private JTextArea detailText;
    private ValidateAction validateAction;
    
    public ValidateFrame(ValidateAction action) {
        super("Validation Results");
        
        this.validateAction = action;
        rendererModel = validateAction.getJcpmodel().getRendererModel();
        getContentPane().setLayout(new BorderLayout());
        
        JPanel southPanel = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new OKAction());
        southPanel.add(okButton);
        
        FieldTablePanel detailPanel = new FieldTablePanel();
        detailText = new JTextArea(2,40);
        detailText.setBackground(Color.white);
        detailText.setWrapStyleWord(true);
        detailText.setText("");
        detailPanel.addArea("Details", detailText);
        detailPanel.validate();

        // set up three tabs, one with statistics, one with errors and 
        // one with warnings (stats omitted for now)
        JTabbedPane tabbedPane = new JTabbedPane();
        errorTreePanel = new JPanel();
        addJTree(new JTree(new DefaultMutableTreeNode("not validated")), errorTreePanel);
        warningTreePanel = new JPanel();
        addJTree(new JTree(new DefaultMutableTreeNode("not validated")), warningTreePanel);
        cdkErrorTreePanel = new JPanel();
        addJTree(new JTree(new DefaultMutableTreeNode("not validated")), cdkErrorTreePanel);
        tabbedPane.add("Errors", errorTreePanel);
        tabbedPane.add("Warnings", warningTreePanel);
        tabbedPane.add("CDK Errors", cdkErrorTreePanel);
        
        getContentPane().add(tabbedPane, BorderLayout.NORTH);
        getContentPane().add(detailPanel, BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        validate();
    }
    
    private void addJTree(JTree tree, JPanel treePanel) {
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        // Listen for when the selection changes.
        final JTree copiedFinal = tree;
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)copiedFinal.getLastSelectedPathComponent();
                
                if (node == null) return;
                
                Object nodeInfo = node.getUserObject();
                if (node.isLeaf() && (nodeInfo instanceof ValidatorErrorNode)) {
                    ValidatorErrorNode errorNode = (ValidatorErrorNode)nodeInfo;
                    detailText.setText(errorNode.getValidationTest().getDetails());
                    if (errorNode instanceof AtomErrorNode) {
                        Atom atom = ((AtomErrorNode)errorNode).getAtom();
                        // highlight atom in JCPFrame
                        rendererModel.setHighlightedAtom(atom);
                    } else if (errorNode instanceof BondErrorNode) {
                        Bond bond = ((BondErrorNode)errorNode).getBond();
                        rendererModel.setHighlightedBond(bond);
                    }
                }
            }
        });

        treePanel.removeAll();
        tree.validate();
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setPreferredSize(new Dimension(400, 350));
        treePanel.add(scrollPane);
    }

    public void validate(IChemObject object) {
        ValidationReport report  = null;
        // logger.debug("Validating class: " + object.getClass().getName());
        if (object instanceof IChemModel) {
            report = validateAction.getValidatorEngine().validateChemModel((ChemModel)object);
        } else if (object instanceof IAtom) {
            report = validateAction.getValidatorEngine().validateAtom((Atom)object);
        } else if (object instanceof IBond) {
            report = validateAction.getValidatorEngine().validateBond((Bond)object);
        } else if (object instanceof Reaction) {
            report = validateAction.getValidatorEngine().validateReaction((Reaction)object);
        } else {
            System.err.println("Cannot validate this object: " + object.getClass().getName());
            return;
        }
        // logger.debug("#errors: " + report.getErrorCount());
        // logger.debug("#warnings: " + report.getWarningCount());
        // logger.debug("#cdk errors: " + report.getCDKErrorCount());
        // logger.debug("#oks: " + report.getOKCount());
        DefaultMutableTreeNode errorsNode = new DefaultMutableTreeNode("errors");
        putErrorsInJTree(errorsNode, report);
        addJTree(new JTree(errorsNode), errorTreePanel);
        DefaultMutableTreeNode warningsNode = new DefaultMutableTreeNode("warnings");
        putWarningsInJTree(warningsNode, report);
        addJTree(new JTree(warningsNode), warningTreePanel);
        DefaultMutableTreeNode cdkErrorsNode = new DefaultMutableTreeNode("errors");
        putCDKErrorsInJTree(cdkErrorsNode, report);
        addJTree(new JTree(cdkErrorsNode), cdkErrorTreePanel);
    }
    
    private void putErrorsInJTree(DefaultMutableTreeNode rootNode, ValidationReport report) {
        // put errors in the tree
        Enumeration errorsEnum = report.getErrors().elements();
        putTestsInJTree(rootNode, errorsEnum);
    }
    
    private void putWarningsInJTree(DefaultMutableTreeNode rootNode, ValidationReport report) {
        // put errors in the tree
        Enumeration errorsEnum = report.getWarnings().elements();
        putTestsInJTree(rootNode, errorsEnum);
    }
    
    private void putCDKErrorsInJTree(DefaultMutableTreeNode rootNode, ValidationReport report) {
        // put CDK errors in the tree
        Enumeration cdkErrorsEnum = report.getCDKErrors().elements();
        putTestsInJTree(rootNode, cdkErrorsEnum);
    }
    
    private void putTestsInJTree(DefaultMutableTreeNode rootNode, Enumeration errors) {
        // put errors in the tree
        while (errors.hasMoreElements()) {
            ValidationTest error = (ValidationTest)errors.nextElement();
            DefaultMutableTreeNode node = new DefaultMutableTreeNode();
            IChemObject object = error.getChemObject();
            if (object instanceof Atom) {
                node.setUserObject(new AtomErrorNode(error, (Atom)object));
                ProblemMarker.markWithError((Atom)object);
            } else if (object instanceof IBond) {
                node.setUserObject(new BondErrorNode(error, (Bond)object));
            } else {
                node.setUserObject(new ValidatorErrorNode(error));
            }
            rootNode.add(node);
        }
    }

    public void closeFrame() {
        dispose();
    }
    
    class OKAction extends AbstractAction {
        OKAction() {
            super("OK");
        }
        
        public void actionPerformed(ActionEvent event) {
            closeFrame();
        }
    }
    
    class ValidatorErrorNode {
        
        ValidationTest test;
        
        ValidatorErrorNode(ValidationTest test) {
            this.test = test;
        }
        
        public String toString() {
            return test.getError();
        }
        
        ValidationTest getValidationTest() {
            return this.test;
        }
    }

    class AtomErrorNode extends ValidatorErrorNode {
        
        Atom atom;
        
        AtomErrorNode(ValidationTest test, Atom atom) {
            super(test);
            this.atom = atom;
        }
        
        Atom getAtom() {
            return this.atom;
        }
    }

    class BondErrorNode extends ValidatorErrorNode {
        
        Bond bond;
        
        BondErrorNode(ValidationTest test, Bond bond) {
            super(test);
            this.bond = bond;
        }
        
        Bond getBond() {
            return this.bond;
        }
    }

}
