import org.codehaus.groovy.maven.mojo.GroovyMojo
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import org.apache.xml.serialize.XMLSerializer
import org.apache.xml.serialize.OutputFormat
import org.w3c.dom.Document
import groovy.xml.*


/**
 * Fixes all module versions in the dependencies of the specified and
 * the de.iritgo.aktera project to match the current version numbers
 * of these modules.
 *
 * All pom.xmls of the current project and the de.iritgo.aktera project
 * are scanned and the module ids and version numbers are collected.
 *
 * In a second run, all dependencies to these modules are fixed, so
 * that they contain the current version numbers of these modules.
 *
 * @goal fixversions
 * @aggregator
 */
public class FixVersionsMojo extends GroovyMojo
{
    void execute()
	{
        def projectPath = new File (".").canonicalPath

        def modules = [:]

        println ("\nScanning modules...\n")

        collectModules (modules, new File (projectPath, "../de.iritgo.aktera"))
        collectModules (modules, new File (projectPath))

        println ("\nFixing versions...\n")

        updateVersions (modules)

        println ()
	}

    def collectModules (modules, path)
    {
        if(path.isDirectory ())
        {
            path.eachFileRecurse
            {
            	if (it.isFile () && it.getName () == 'pom.xml')
            	{
            		readPom (modules, it)
            	}
            }
        }
    }

    def readPom (modules, file)
    {
		def ns = new groovy.xml.Namespace("http://maven.apache.org/POM/4.0.0", 'ns')
        def pom = new XmlParser ().parse (file)
        def module = [:]
        module.groupId = pom[ns.groupId].text ()
        module.artifactId = pom[ns.artifactId].text ()
        module.version = pom[ns.version].text ()
        module.pomFile = file
        println ("${module.groupId}.${module.artifactId} ${module.version}")
        modules[module.groupId + '.' + module.artifactId] = module
    }

    def updateVersions (modules)
    {
		def ns = new groovy.xml.Namespace("http://maven.apache.org/POM/4.0.0", 'ns')
        modules.each
        {
            id, module ->
            println (id)
            def pom = new XmlParser ().parse (module.pomFile)
            def modified = false
            for (dependency in pom[ns.dependencies][ns.dependency])
            {
            	def depId = dependency[ns.groupId].text () + "." + dependency[ns.artifactId].text ()
            	print ("\t${depId}")
            	if (! modules.containsKey (depId))
            	{
            		println (" Skipped")
            		continue
            	}
            	else if (modules[depId].version == dependency[ns.version].text ())
            	{
            		println (" ${dependency[ns.version].text ()} Ok")
            		continue
            	}
            	print (" ${dependency[ns.version].text ()} WRONG")
            	dependency[ns.version][0].value = modules[depId].version
            	println (" Fixed (${dependency[ns.version].text ()})")
            	modified = true
            }
            if (modified)
            {
            	def newPom = new File (module.pomFile.canonicalPath + ".new")
            	newPom.withPrintWriter ()
            	{
            		writer ->

            		def inHeader = true
            		module.pomFile.eachLine
            		{
            			if (it.startsWith ("<project"))
            			{
            				inHeader = false
            			}
            			if (inHeader)
            			{
            				writer.println (it)
            			}
            		}

					def xmlNodePrinter = new XmlNodePrinter(new PrintWriter (writer), "\t")
					xmlNodePrinter.preserveWhitespace = true
					xmlNodePrinter.print (pom)
            	}
            	newPom.renameTo (module.pomFile)
            }
        }
    }
}
