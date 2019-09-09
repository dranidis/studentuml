import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import org.junit.Test;

import edu.city.studentuml.frame.StudentUMLFrame;
import edu.city.studentuml.model.domain.Attribute;
import edu.city.studentuml.model.domain.DataType;
import edu.city.studentuml.model.domain.DesignClass;
import edu.city.studentuml.model.domain.Interface;
import edu.city.studentuml.model.domain.Method;
import edu.city.studentuml.model.domain.MethodParameter;
import edu.city.studentuml.model.domain.MultiObject;
import edu.city.studentuml.model.domain.SDObject;
import edu.city.studentuml.model.domain.Type;
import edu.city.studentuml.model.domain.UMLProject;
import edu.city.studentuml.util.CodeGenerator;
import edu.city.studentuml.view.gui.ApplicationFrame;

public class CodeGeneratorTest {

	@Test
	public void testGenerateClassFile() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		File f = new File(path);
		assertTrue(path.contains("Class1"));
		assertTrue(f.exists() && !f.isDirectory());
	}
	
	@Test
	public void testGenerateInterfaceFile() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new Interface("Int1");
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		File f = new File(path);
		assertTrue(path.contains("Int1"));
		assertTrue(f.exists() && !f.isDirectory());
	}

	@Test
	public void testGenerateClassStart() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		File f = new File(path);
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
		assertTrue(classExists);
	}
	
	@Test
	public void testGenerateInterfaceStart() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new Interface("Int1");
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		File f = new File(path);
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
		assertTrue(interfaceExists);
	}
	
	@Test
	public void testGenerateAbstractClassStart() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		DesignClass dc= (DesignClass) classObject;
		dc.setStereotype("abstract");
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(dc,projectPath,umlProject);
		File f = new File(path);
		boolean abstractClassExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("abstract class Class1")) {
        				abstractClassExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(abstractClassExists);
	}
	
	@Test
	public void testGenerateImplementInterfaceStart() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		DesignClass dc= (DesignClass) classObject;
		dc.resetImplementInterfaces();
		dc.addImplementInterfaces(new Interface("Int1"));
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(dc,projectPath,umlProject);
		File f = new File(path);
		boolean implementInterfaceExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("class Class1 implements Int1")) {
        				implementInterfaceExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(implementInterfaceExists);
	}

	@Test
	public void testGenerateExtendClassStart() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		DesignClass dc= (DesignClass) classObject;
		DesignClass adc= new DesignClass("AbstractClass1");
		adc.setStereotype("abstract");
		dc.setExtendClass(adc);
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(dc,projectPath,umlProject);
		File f = new File(path);
		boolean extendClassExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("class Class1 extends AbstractClass1")) {
        				extendClassExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(extendClassExists);
	}
	
	@Test
	public void testGenerateOperation() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		DesignClass dc = (DesignClass) classObject;
		Method mtd1 = new Method("mtd1");
		mtd1.setVisibility(2);
		mtd1.setReturnType(new DataType("int"));
		mtd1.addParameter(new MethodParameter("x",new DataType("int")));
		dc.addMethod(mtd1);
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		File f = new File(path);
		boolean methodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("public int mtd1(int x)")) {
        				methodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(methodExists);
	}
	
	@Test
	public void testGenerateSDOperation() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		DesignClass dc = (DesignClass) classObject;
		Method mtd1 = new Method("mtd1");
		mtd1.setVisibility(2);
		mtd1.setReturnType(new DataType("int"));
		mtd1.addParameter(new MethodParameter("x",new DataType("int")));
		dc.addSDMethod(mtd1);
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		File f = new File(path);
		boolean methodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("public int mtd1(int x)")) {
        				methodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(methodExists);
	}

	@Test
	public void testGenerateCoreAttribute() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		DesignClass dc = (DesignClass) classObject;
		dc.addAttribute(new Attribute("attr1",new DataType("String")));
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		File f = new File(path);
		boolean attributeExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("String attr1")) {
        				attributeExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(attributeExists);
	
	}

	@Test
	public void testGenerateCalledMethods() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		DesignClass dc = (DesignClass) classObject;
		DesignClass dc2 = new DesignClass("Class2");
		Method headMethod = new Method("hmtd");
		Method mtd1 = new Method("mtd1");
		mtd1.setVisibility(2);
		mtd1.setReturnType(new DataType("int"));
		mtd1.addParameter(new MethodParameter("x",new DataType("int")));
		headMethod.addCalledMethod(mtd1, dc2, new SDObject("sd2",dc2),false);
		dc.addMethod(headMethod);
		dc2.addSDMethod(mtd1);
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		File f = new File(path);
		boolean calledBranchedMethodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("x = sd2.mtd1(x)")) {
        				calledBranchedMethodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(calledBranchedMethodExists);
	}
	
	@Test
	public void testGenerateVoidCalledMethods() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		DesignClass dc = (DesignClass) classObject;
		DesignClass dc2 = new DesignClass("Class2");
		Method headMethod = new Method("hmtd");
		Method mtd1 = new Method("mtd1");
		mtd1.setVisibility(2);
		mtd1.setReturnType(new DataType("void"));
		mtd1.addParameter(new MethodParameter("x",new DataType("int")));
		headMethod.addCalledMethod(mtd1, dc2, new SDObject("sd2",dc2),false);
		dc.addMethod(headMethod);
		dc2.addSDMethod(mtd1);
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		File f = new File(path);
		boolean calledVoidMethodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(!line.contains("x =") && line.contains("sd2.mtd1(x)")) {
        				calledVoidMethodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(calledVoidMethodExists);
	}
	
	@Test
	public void testGenerateMultiobjectCalledMethods() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		DesignClass dc = (DesignClass) classObject;
		DesignClass dc2 = new DesignClass("Class2");
		Method headMethod = new Method("hmtd");
		Method mtd1 = new Method("mtd1");
		mtd1.setVisibility(2);
		mtd1.setReturnType(new DataType("int"));
		mtd1.addParameter(new MethodParameter("x",new DataType("int")));
		headMethod.addCalledMethod(mtd1, dc2, new MultiObject("sd2Array",dc2),false);
		dc.addMethod(headMethod);
		dc2.addSDMethod(mtd1);
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		File f = new File(path);
		boolean multiobjectCalledMethodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("x = sd2Array.mtd1(x)")) {
        				multiobjectCalledMethodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(multiobjectCalledMethodExists);
	}
	
	@Test
	public void testGenerateSelfCalledMethods() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		DesignClass dc = (DesignClass) classObject;
		Method headMethod = new Method("hmtd");
		Method mtd1 = new Method("mtd1");
		mtd1.setVisibility(2);
		mtd1.setReturnType(new DataType("int"));
		mtd1.addParameter(new MethodParameter("x",new DataType("int")));
		headMethod.addCalledMethod(mtd1, dc, new SDObject("sd1",dc),true);
		dc.addMethod(headMethod);
		dc.addSDMethod(mtd1);
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		File f = new File(path);
		boolean selfCalledMethodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("x = this.mtd1(x)")) {
        				selfCalledMethodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(selfCalledMethodExists);
	}
	
	@Test
	public void testGenerateIterativeCalledMethods() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = new DesignClass("Class1");
		DesignClass dc = (DesignClass) classObject;
		DesignClass dc2 = new DesignClass("Class2");
		Method headMethod = new Method("hmtd");
		Method mtd1 = new Method("mtd1");
		mtd1.setVisibility(2);
		mtd1.setReturnType(new DataType("int"));
		mtd1.addParameter(new MethodParameter("x",new DataType("int")));
		mtd1.setIterative(true);
		headMethod.addCalledMethod(mtd1, dc2, new SDObject("sd2",dc2),false);
		dc.addMethod(headMethod);
		dc2.addSDMethod(mtd1);
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		File f = new File(path);
		boolean iterativeCalledMethodExists = false;
		if (!f.isDirectory() && f.exists()) {
        	try {
        		String line = null;
        		FileReader fr = new FileReader(f);
        		BufferedReader br = new BufferedReader(fr);
        		while((line=br.readLine()) != null) {
        			if(line.contains("for(int i=0;i<10;i++){")) {
        				iterativeCalledMethodExists=true;
        			}
        		}
        		fr.close();
        		br.close();
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
		}
		assertTrue(iterativeCalledMethodExists);
	}
	

	
	@Test
	public void testNoInputGenerateFile() {
		StudentUMLFrame studentUMLFrame =  StudentUMLFrame.getInstance();
		ApplicationFrame af = new ApplicationFrame(studentUMLFrame);
		UMLProject umlProject = UMLProject.getInstance();
		umlProject.clear();
		File file = new File("C:\\test");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Directory cannot be Created!");
            }
        }
		umlProject.setFilepath("C:\\test\\test.xml");
		umlProject.setFilename("test.xml");
		Object classObject = null;
		af.saveProject();
		String projectPath = new File(umlProject.getFilepath()).getParent();
		CodeGenerator testGenerator =new CodeGenerator();
		String path = testGenerator.generateFile(classObject,projectPath,umlProject);
		assertTrue(path==null);
	}
}
