import static org.junit.Assert.*;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import org.junit.Test;

import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.model.domain.Association;
import edu.city.studentuml.model.domain.CallMessage;
import edu.city.studentuml.model.domain.CreateMessage;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.DestroyMessage;
import edu.city.studentuml.model.domain.Generalization;
import edu.city.studentuml.model.domain.GenericOperation;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.MessageParameter;
import edu.city.studentuml.model.domain.MessageReturnValue;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.Realization;
import edu.city.studentuml.model.domain.ReturnMessage;
import edu.city.studentuml.model.domain.Role;
import edu.city.studentuml.model.domain.RoleClassifier;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.model.graphical.AssociationGR;
import edu.city.studentuml.model.graphical.CallMessageGR;
import edu.city.studentuml.model.graphical.ClassGR;
import edu.city.studentuml.model.graphical.CreateMessageGR;
import edu.city.studentuml.model.graphical.DCDModel;
import edu.city.studentuml.model.graphical.DestroyMessageGR;
import edu.city.studentuml.model.graphical.GraphicalElement;
import edu.city.studentuml.model.graphical.InterfaceGR;
import edu.city.studentuml.model.graphical.RealizationGR;
import edu.city.studentuml.model.graphical.ReturnMessageGR;
import edu.city.studentuml.model.graphical.SDModel;
import edu.city.studentuml.model.graphical.SDObjectGR;
import edu.city.studentuml.model.graphical.GeneralizationGR;
import edu.city.studentuml.view.gui.ApplicationFrame;

public class UMLProjectTest {
	
	String filepath = "test";
	String fullpath = filepath + File.separator + "test.xml";
	String javapath = filepath + File.separator + filepath + File.separator;

	@Test
	public void testGenerateClassFromDCD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		DCDModel currDiagram = new DCDModel("dcd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		currDiagram.addGraphicalElement(new ClassGR(dc1,new Point(1,10)));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class1.java");
		boolean classExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("class Class1")) {
        				classExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(classExists && generatedFiles==1);
	}
	
	@Test
	public void testGenerateInterfaceFromDCD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		DCDModel currDiagram = new DCDModel("dcd1",umlProject);
		Interface int1 = new Interface("Int1");
		currDiagram.addGraphicalElement(new InterfaceGR(int1,new Point(1,10)));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Int1.java");
		boolean interfaceExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("interface Int1")) {
        				interfaceExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(interfaceExists && generatedFiles==1);
	}
	
	@Test
	public void testGenerateClassFromSD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		SDModel currDiagram = new SDModel("sd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		currDiagram.addGraphicalElement(new SDObjectGR(new SDObject("sd1",dc1),1));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class1.java");
		boolean classExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("class Class1")) {
        				classExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(classExists && generatedFiles==1);
	}
	
	@Test
	public void testGenerateMethodFromSD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		SDModel currDiagram = new SDModel("sd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		DesignClass dc2 = new DesignClass("Class2");
		GenericOperation mtd1 = new GenericOperation("mtd1");
		SDObject sd1 = new SDObject("sd1",dc1);
		SDObject sd2 = new SDObject("sd2",dc2);
		SDObjectGR sd1GR = new SDObjectGR(sd1,1);
		SDObjectGR sd2GR = new SDObjectGR(sd2,2);
		currDiagram.addGraphicalElement(sd1GR);
		currDiagram.addGraphicalElement(sd2GR);
		CallMessage cm = new CallMessage(sd1,sd2,mtd1);
		cm.setReturnValue(new MessageReturnValue("int"));
		cm.addParameter(new MessageParameter("int x"));
		currDiagram.addGraphicalElement(new CallMessageGR(sd1GR,sd2GR,cm,1));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class2.java");
		boolean methodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("int mtd1(int x)")) {
        				methodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(methodExists && generatedFiles==2);
	}
	
	@Test
	public void testGenerateMethodFromMultipleSD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		SDModel currDiagram = new SDModel("sd1",umlProject);
		SDModel currDiagram2 = new SDModel("sd2",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		DesignClass dc2 = new DesignClass("Class2");
		GenericOperation mtd1 = new GenericOperation("mtd1");
		GenericOperation mtd2 = new GenericOperation("mtd2");
		SDObject sd1 = new SDObject("sd1",dc1);
		SDObject sd2 = new SDObject("sd2",dc2);
		SDObjectGR sd1GR = new SDObjectGR(sd1,1);
		SDObjectGR sd2GR = new SDObjectGR(sd2,2);
		currDiagram.addGraphicalElement(sd1GR);
		currDiagram.addGraphicalElement(sd2GR);
		CallMessage cm = new CallMessage(sd1,sd2,mtd1);
		cm.setReturnValue(new MessageReturnValue("int"));
		cm.addParameter(new MessageParameter("int x"));
		currDiagram.addGraphicalElement(new CallMessageGR(sd1GR,sd2GR,cm,1));
		currDiagram2.addGraphicalElement(sd1GR);
		currDiagram2.addGraphicalElement(sd2GR);
		CallMessage cm2 = new CallMessage(sd1,sd2,mtd2);
		cm2.setReturnValue(new MessageReturnValue("void"));
		cm2.addParameter(new MessageParameter("String x"));
		currDiagram2.addGraphicalElement(new CallMessageGR(sd1GR,sd2GR,cm2,1));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class2.java");
		boolean method1Exists = false;
		boolean method2Exists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("int mtd1(int x)")) {
        				method1Exists=true;
        			}
        			if(line.contains("void mtd2(String x)")) {
        				method2Exists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(method1Exists && method2Exists && generatedFiles==2);
	}
	
	@Test
	public void testGenerateMethodFromDCDandSD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		DesignClass dc1 = new DesignClass("Class1");
		DesignClass dc2 = new DesignClass("Class2");
		DCDModel dcdDiagram = new DCDModel("dcd1",umlProject);
		dc2.addMethod(new Method("mtd4"));
		dcdDiagram.addGraphicalElement(new ClassGR(dc1,new Point(1,10)));
		dcdDiagram.addGraphicalElement(new ClassGR(dc2,new Point(2,20)));
		SDModel sdDiagram = new SDModel("sd1",umlProject);
		GenericOperation mtd1 = new GenericOperation("mtd1");
		SDObject sd1 = new SDObject("sd1",dc1);
		SDObject sd2 = new SDObject("sd2",dc2);
		SDObjectGR sd1GR = new SDObjectGR(sd1,1);
		SDObjectGR sd2GR = new SDObjectGR(sd2,2);
		sdDiagram.addGraphicalElement(sd1GR);
		sdDiagram.addGraphicalElement(sd2GR);
		CallMessage cm = new CallMessage(sd1,sd2,mtd1);
		cm.setReturnValue(new MessageReturnValue("int"));
		cm.addParameter(new MessageParameter("int x"));
		sdDiagram.addGraphicalElement(new CallMessageGR(sd1GR,sd2GR,cm,1));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class2.java");
		boolean sdMethodExists = false;
		boolean dcdMethodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("int mtd1(int x)")) {
        				sdMethodExists=true;
        			}
        			if(line.contains("mtd4")) {
        				dcdMethodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(sdMethodExists && dcdMethodExists &&  generatedFiles==2);
	}
	
	@Test
	public void testGenerateCalledMethodFromSD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		SDModel currDiagram = new SDModel("sd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		DesignClass dc2 = new DesignClass("Class2");
		DesignClass dc3 = new DesignClass("Class3");
		GenericOperation mtd1 = new GenericOperation("mtd1");
		GenericOperation mtd2 = new GenericOperation("mtd2");
		SDObject sd1 = new SDObject("sd1",dc1);
		SDObject sd2 = new SDObject("sd2",dc2);
		SDObject sd3 = new SDObject("sd3",dc3);
		SDObjectGR sd1GR = new SDObjectGR(sd1,1);
		SDObjectGR sd2GR = new SDObjectGR(sd2,2);
		SDObjectGR sd3GR = new SDObjectGR(sd3,3);
		currDiagram.addGraphicalElement(sd1GR);
		currDiagram.addGraphicalElement(sd2GR);
		currDiagram.addGraphicalElement(sd3GR);
		CallMessage cm1 = new CallMessage(sd1,sd2,mtd1);
		cm1.setReturnValue(new MessageReturnValue("int"));
		cm1.addParameter(new MessageParameter("int y"));
		currDiagram.addGraphicalElement(new CallMessageGR(sd1GR,sd2GR,cm1,1));
		CallMessage cm2 = new CallMessage(sd2,sd3,mtd2);
		cm2.setReturnValue(new MessageReturnValue("void"));
		cm2.addParameter(new MessageParameter("int y"));
		currDiagram.addGraphicalElement(new CallMessageGR(sd2GR,sd3GR,cm2,2));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class2.java");
		boolean calledMethodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("sd3.mtd2(y)")) {
        				calledMethodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(calledMethodExists && generatedFiles==3);
	}
	
	@Test
	public void testGenerateCalledMethodWithReturnParameterFromSD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		SDModel currDiagram = new SDModel("sd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		DesignClass dc2 = new DesignClass("Class2");
		DesignClass dc3 = new DesignClass("Class3");
		GenericOperation mtd1 = new GenericOperation("mtd1");
		GenericOperation mtd2 = new GenericOperation("mtd2");
		SDObject sd1 = new SDObject("sd1",dc1);
		SDObject sd2 = new SDObject("sd2",dc2);
		SDObject sd3 = new SDObject("sd3",dc3);
		SDObjectGR sd1GR = new SDObjectGR(sd1,1);
		SDObjectGR sd2GR = new SDObjectGR(sd2,2);
		SDObjectGR sd3GR = new SDObjectGR(sd3,3);
		currDiagram.addGraphicalElement(sd1GR);
		currDiagram.addGraphicalElement(sd2GR);
		currDiagram.addGraphicalElement(sd3GR);
		CallMessage cm1 = new CallMessage(sd1,sd2,mtd1);
		cm1.setReturnValue(new MessageReturnValue("int"));
		cm1.addParameter(new MessageParameter("int y"));
		currDiagram.addGraphicalElement(new CallMessageGR(sd1GR,sd2GR,cm1,1));
		CallMessage cm2 = new CallMessage(sd2,sd3,mtd2);
		cm2.setReturnValue(new MessageReturnValue("int"));
		cm2.addParameter(new MessageParameter("int y"));
		currDiagram.addGraphicalElement(new CallMessageGR(sd2GR,sd3GR,cm2,2));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class2.java");
		boolean calledMethodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("x = sd3.mtd2(y)")) {
        				calledMethodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(calledMethodExists && generatedFiles==3);
	}
	
	@Test
	public void testGenerateCreateMethodFromSD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		SDModel currDiagram = new SDModel("sd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		DesignClass dc2 = new DesignClass("Class2");
		DesignClass dc3 = new DesignClass("Class3");
		GenericOperation mtd1 = new GenericOperation("mtd1");
		GenericOperation mtd2 = new GenericOperation("mtd2");
		SDObject sd1 = new SDObject("sd1",dc1);
		SDObject sd2 = new SDObject("sd2",dc2);
		SDObject sd3 = new SDObject("sd3",dc3);
		SDObjectGR sd1GR = new SDObjectGR(sd1,1);
		SDObjectGR sd2GR = new SDObjectGR(sd2,2);
		SDObjectGR sd3GR = new SDObjectGR(sd3,3);
		currDiagram.addGraphicalElement(sd1GR);
		currDiagram.addGraphicalElement(sd2GR);
		currDiagram.addGraphicalElement(sd3GR);
		CallMessage cm1 = new CallMessage(sd1,sd2,mtd1);
		cm1.setReturnValue(new MessageReturnValue("int"));
		cm1.addParameter(new MessageParameter("int y"));
		currDiagram.addGraphicalElement(new CallMessageGR(sd1GR,sd2GR,cm1,1));
		CreateMessage cm2 = new CreateMessage(sd2,sd3);
		currDiagram.addGraphicalElement(new CreateMessageGR(sd2GR,sd3GR,cm2,2));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class2.java");
		boolean calledMethodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("sd3 = new Class3()")) {
        				calledMethodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(calledMethodExists && generatedFiles==3);
	}
	
	@Test
	public void testGenerateDestroyMethodFromSD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		SDModel currDiagram = new SDModel("sd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		DesignClass dc2 = new DesignClass("Class2");
		DesignClass dc3 = new DesignClass("Class3");
		GenericOperation mtd1 = new GenericOperation("mtd1");
		GenericOperation mtd2 = new GenericOperation("mtd2");
		SDObject sd1 = new SDObject("sd1",dc1);
		SDObject sd2 = new SDObject("sd2",dc2);
		SDObject sd3 = new SDObject("sd3",dc3);
		SDObjectGR sd1GR = new SDObjectGR(sd1,1);
		SDObjectGR sd2GR = new SDObjectGR(sd2,2);
		SDObjectGR sd3GR = new SDObjectGR(sd3,3);
		currDiagram.addGraphicalElement(sd1GR);
		currDiagram.addGraphicalElement(sd2GR);
		currDiagram.addGraphicalElement(sd3GR);
		CallMessage cm1 = new CallMessage(sd1,sd2,mtd1);
		cm1.setReturnValue(new MessageReturnValue("int"));
		cm1.addParameter(new MessageParameter("int y"));
		currDiagram.addGraphicalElement(new CallMessageGR(sd1GR,sd2GR,cm1,1));
		DestroyMessage cm2 = new DestroyMessage(sd2,sd3);
		currDiagram.addGraphicalElement(new DestroyMessageGR(sd2GR,sd3GR,cm2,2));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class2.java");
		boolean calledMethodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("sd3.destroy()")) {
        				calledMethodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(calledMethodExists && generatedFiles==3);
	}
	
	@Test
	public void testNotGenerateForNoInput() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		assertTrue(generatedFiles==0);
	}
	
	@Test
	public void testGenerateExtendClassFromDCD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		DCDModel currDiagram = new DCDModel("dcd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		DesignClass adc = new DesignClass("AbClass");
		adc.setStereotype("abstract");
		ClassGR dc1gr = new ClassGR(dc1,new Point(1,10));
		ClassGR adcgr = new ClassGR(adc,new Point(2,20));
		currDiagram.addGraphicalElement(dc1gr);
		currDiagram.addGraphicalElement(adcgr);
		currDiagram.addGraphicalElement(new GeneralizationGR(adcgr,dc1gr,new Generalization(adc,dc1)));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class1.java");
		boolean classExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("class Class1 extends AbClass")) {
        				classExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(classExists && generatedFiles==2);
	}
	
	@Test
	public void testGenerateImplementInterfaceFromDCD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		DCDModel currDiagram = new DCDModel("dcd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		Interface int1= new Interface("Int1");
		ClassGR dc1gr = new ClassGR(dc1,new Point(1,10));
		InterfaceGR int1gr = new InterfaceGR(int1,new Point(2,20));
		currDiagram.addGraphicalElement(dc1gr);
		currDiagram.addGraphicalElement(int1gr);
		currDiagram.addGraphicalElement(new RealizationGR(dc1gr,int1gr,new Realization(dc1,int1)));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class1.java");
		boolean classExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("class Class1 implements Int1")) {
        				classExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(classExists && generatedFiles==2);
	}
	
	@Test
	public void testGenerateImplementMultipleInterfacesFromDCD() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		DCDModel currDiagram = new DCDModel("dcd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		Interface int1= new Interface("Int1");
		Interface int2= new Interface("Int2");
		ClassGR dc1gr = new ClassGR(dc1,new Point(1,10));
		InterfaceGR int1gr = new InterfaceGR(int1,new Point(2,20));
		InterfaceGR int2gr = new InterfaceGR(int2,new Point(3,30));
		currDiagram.addGraphicalElement(dc1gr);
		currDiagram.addGraphicalElement(int1gr);
		currDiagram.addGraphicalElement(int2gr);
		currDiagram.addGraphicalElement(new RealizationGR(dc1gr,int1gr,new Realization(dc1,int1)));
		currDiagram.addGraphicalElement(new RealizationGR(dc1gr,int2gr,new Realization(dc1,int2)));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class1.java");
		boolean classExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("class Class1 implements Int1,Int2")) {
        				classExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(classExists && generatedFiles==3);
	}
	
	@Test
	public void testGenerateAssociationAttributeAtoB() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		DCDModel currDiagram = new DCDModel("dcd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		DesignClass dc2 = new DesignClass("Class2");
		ClassGR dc1GR = new ClassGR(dc1,new Point(1,10));
		ClassGR dc2GR = new ClassGR(dc2,new Point(10,20));
		currDiagram.addGraphicalElement(dc1GR);
		currDiagram.addGraphicalElement(dc2GR);
		Role dc1Role = new Role(dc1);
		dc1Role.setName("sd1");
		Role dc2Role = new Role(dc2);
		dc2Role.setName("sd2");
		Association AtoB = new Association(dc1Role,dc2Role);
		AtoB.setDirection(1);
		AtoB.setShowArrow(true);
		currDiagram.addGraphicalElement(new AssociationGR(dc1GR,dc2GR,AtoB));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class1.java");
		boolean parameterExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("Class2 sd2;")) {
        				parameterExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(parameterExists && generatedFiles==2);
	}
	
	@Test
	public void testGenerateAssociationAttributeBtoA() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		DCDModel currDiagram = new DCDModel("dcd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		DesignClass dc2 = new DesignClass("Class2");
		ClassGR dc1GR = new ClassGR(dc1,new Point(1,10));
		ClassGR dc2GR = new ClassGR(dc2,new Point(10,20));
		currDiagram.addGraphicalElement(dc1GR);
		currDiagram.addGraphicalElement(dc2GR);
		Role dc1Role = new Role(dc1);
		dc1Role.setName("sd1");
		Role dc2Role = new Role(dc2);
		dc2Role.setName("sd2");
		Association BtoA = new Association(dc1Role,dc2Role);
		BtoA.setDirection(2);
		BtoA.setShowArrow(true);
		currDiagram.addGraphicalElement(new AssociationGR(dc1GR,dc2GR,BtoA));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		File f = new File(javapath+"Class2.java");
		boolean parameterExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("Class1 sd1;")) {
        				parameterExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(parameterExists && generatedFiles==2);
	}
	
	@Test
	public void testGenerateAssociationAttributeBidirectional() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File(filepath);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath(fullpath);
		umlProject.setFilename("test.xml");
		DCDModel currDiagram = new DCDModel("dcd1",umlProject);
		DesignClass dc1 = new DesignClass("Class1");
		DesignClass dc2 = new DesignClass("Class2");
		ClassGR dc1GR = new ClassGR(dc1,new Point(1,10));
		ClassGR dc2GR = new ClassGR(dc2,new Point(10,20));
		currDiagram.addGraphicalElement(dc1GR);
		currDiagram.addGraphicalElement(dc2GR);
		Role dc1Role = new Role(dc1);
		dc1Role.setName("sd1");
		Role dc2Role = new Role(dc2);
		dc2Role.setName("sd2");
		Association bi = new Association(dc1Role,dc2Role);
		bi.setDirection(3);
		bi.setShowArrow(true);
		currDiagram.addGraphicalElement(new AssociationGR(dc1GR,dc2GR,bi));
		af.saveProject();
		int generatedFiles = umlProject.generateCode(false);
		int parameterExists = 0;
		File f = new File(javapath+"Class1.java");		
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("Class2 sd2;")) {
        				parameterExists++;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}   	
		}
		File f2 = new File(javapath+"Class2.java");		
		if (!f2.isDirectory() && f2.exists()) {
        	try {
        		String line2 = null;
        		FileReader fr2 = new FileReader(f2);
        		BufferedReader br2 = new BufferedReader(fr2);
        		while((line2=br2.readLine()) != null) {
        			if(line2.contains("Class1 sd1;")) {
        				parameterExists++;
        			}
        		}
        		fr2.close();
        		br2.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}   	
		}
		assertTrue(parameterExists==2 && generatedFiles==2);
	}
	
	
	
}
