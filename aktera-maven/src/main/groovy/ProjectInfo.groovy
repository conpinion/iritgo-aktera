import org.codehaus.groovy.maven.mojo.GroovyMojo
import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import org.apache.maven.project.MavenProject;


/**
 * @goal projectinfo
 * @aggregator
 */
public class ProjectInfoMojo extends GroovyMojo
{
	/** @parameter expression="${project}" */
	MavenProject project;
	
	def quitter = new Object ()

    void execute() 
	{
		println project.modules;
		
		/*
    	def swing = new SwingBuilder ()
    	def count = 0
    	def textlabel
    	def frame = swing.frame (title: 'Frame', size: [800, 600], 
			windowClosing: { synchronized (quitter) { quitter.notify () } }) 
    	{
			borderLayout ()
			textlabel = label (text:"Clicked ${count} time(s).", constraints: BorderLayout.NORTH)
    	  	button (text:'Click Me',
				actionPerformed: { count++; textlabel.text = "Clicked ${count} time(s)."; println "clicked" },
				constraints:BorderLayout.SOUTH)		
    	}
    	frame.pack()
    	frame.show()
    	try
    	{
			synchronized (quitter) { quitter.wait () }
    	}
    	catch (Exception ignored)
    	{   		
    	}
    	*/
    }
}
