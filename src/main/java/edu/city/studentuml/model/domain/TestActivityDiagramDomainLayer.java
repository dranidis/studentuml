package edu.city.studentuml.model.domain;

import java.util.Iterator;

/**
 *
 * @author Biser
 */
public class TestActivityDiagramDomainLayer {

    public static void print(Object o) {
        java.lang.System.out.println(o);
    }

    public static void main(String[] args) {
        print("\n------Initial Node tests------");
        InitialNode a = new InitialNode();
        print(a);

        NodeComponent b = new InitialNode();
        print(b);

        Iterator it = b.createIterator();
        print(it.hasNext());

        print(a.getName());
        //b.setName("Blah");

        //b.add(a);
        //b.remove(a);

        print("\n------Final Node tests------");
        FinalNode c = new ActivityFinalNode();
        print(c);

        NodeComponent d = new ActivityFinalNode();
        print(d);

        Iterator it2 = d.createIterator();
        print(it2.hasNext());

        //c.setName("Blah");
        print(d.getName());

        //c.add(d);
        //d.remove(c);

        print("\n------Action Node tests------");
        ActionNode e = new ActionNode();
        e.setName("Action blah bleh blih");
        print(e);

        NodeComponent f = new ActionNode();
        f.setName("Action blih bleh bluh");
        print(f);

        Iterator i3 = e.createIterator();
        print(i3.hasNext());

        e.setName("Action Grrr");
        print(e.getName());

        //e.add(f);
        //f.remove(e);

        print("\n------Object Node tests------");
        ObjectNode g = new ObjectNode();
        print(g);
        g.setName("ObjectBlah");
        print(g);
        g.setName("");
        print(g);

        Type type = new DesignClass("Class1");
        g.setType(type);
        print(g);
        g.setName("ObjectNode");
        print(g);

        print(g + " has states? " + g.hasStates());
        print(g.getStatesAsString());
        State state = new State("State1");
        g.addState(state);
        print(g + " has states? " + g.hasStates());
        print(g.getStatesAsString());
        State state2 = new State("State2");
        g.addState(state2);
        print(g + " has states? " + g.hasStates());
        print(g.getStatesAsString());
        g.removeState(state);
        print(g + " has states? " + g.hasStates());
        print(g.getStatesAsString());
        g.removeState(state2);
        print(g + " has states? " + g.hasStates());
        print(g.getStatesAsString());

        NodeComponent h = new ObjectNode();
        h.setName("Object Blih bloh bluh");
        print(h);

        Iterator i4 = g.createIterator();
        print(i4.hasNext());

        g.setName("ObjectGrrr");
        print(g.getName());

        //g.add(h);
        //h.remove(g);

        // Test activity nodes
        print("\n------Activity Node tests------");
        NodeComponent oneActivity = new ActivityNode();
        oneActivity.setName("One");
        NodeComponent twoActivity = new ActivityNode();
        twoActivity.setName("Two");
        NodeComponent threeActivity = new ActivityNode();
        threeActivity.setName("Three");
        NodeComponent fourActivity = new ActivityNode();
        fourActivity.setName("Four");

        NodeComponent allNodes = new ActivityNode();
        allNodes.setName("All");
        allNodes.add(oneActivity);
        allNodes.add(twoActivity);
        allNodes.add(threeActivity);

        NodeComponent actionOne = new ActionNode();
        actionOne.setName("actionOne");
        oneActivity.add(actionOne);

        NodeComponent actionTwo = new ActionNode();
        actionTwo.setName("actionTwo");
        twoActivity.add(actionTwo);
        twoActivity.add(fourActivity);

        NodeComponent actionThree = new ActionNode();
        actionThree.setName("actionThree");
        fourActivity.add(actionThree);
        
        NodeComponent actionFour = new ActionNode();
        actionFour.setName("actionFour");
        NodeComponent actionFive = new ActionNode();
        actionFive.setName("actionFive");
        threeActivity.add(actionFour);
        threeActivity.add(actionFive);

        // BUG  in the method next (fixed)
        NodeComponent fiveActivity = new ActivityNode();
        fiveActivity.setName("Five");
        NodeComponent sixActivity = new ActivityNode();
        sixActivity.setName("Six");
        NodeComponent actionSix = new ActionNode();
        actionSix.setName("actionSix");
        threeActivity.add(fiveActivity);
        fiveActivity.add(sixActivity);
        sixActivity.add(actionSix);
        NodeComponent actionSeven = new ActionNode();
        actionSeven.setName("actionSeven");
        threeActivity.add(actionSeven);

        print("\n" + allNodes.getName());
        print("------------------------");

        Iterator iterator = allNodes.createIterator();
        while (iterator.hasNext()) {
            NodeComponent nodeComponent = (NodeComponent) iterator.next();
            print(nodeComponent);
        }


        threeActivity.remove(fiveActivity);

        print("------------------------");
        print("After Removals");
        print("\n" + allNodes.getName());
        print("------------------------");

        iterator = allNodes.createIterator();
        while (iterator.hasNext()) {
            NodeComponent nodeComponent = (NodeComponent) iterator.next();
            print(nodeComponent);
        }
    }
}
