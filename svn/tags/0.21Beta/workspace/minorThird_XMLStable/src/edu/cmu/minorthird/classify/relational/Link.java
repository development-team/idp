/* Copyright 2006, Carnegie Mellon, All Rights Reserved */

package edu.cmu.minorthird.classify.relational;

import edu.cmu.minorthird.util.AbstractLooper;
import edu.cmu.minorthird.util.gui.Viewer;
import edu.cmu.minorthird.util.gui.Visible;

import java.util.Collection;
import java.util.Iterator;
import java.io.*;

/** A link has 'from' 'to' 'type' fields, more attrs can be added

 * @author Zhenzhen Kou
 */

public class Link
{

    protected String from;
    protected String to;
    protected String type;
    
		
    public Link(String from, String to, String type) 
    {
	
	this.from = from;
	this.to = to;
	this.type = type;
    }

    /** get the from ExampleID */
    public String getFrom() { return from; }

    /** Get the to ExampleID */
    public String getTo() { return to; }

    /** Get the link type.
     */
    public String getType() { return type; }


    public String toString() { return "[Link: "+from+" "+to+" "+type+"]"; }


}
