<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml"/>


<xsl:template match="jasperPrint">

	<table>

		<tr>
        <xsl:apply-templates select="body/body.head"/>
		</tr>

        <xsl:apply-templates select="body/body.head"/>

	</table>

</xsl:template>


</xsl:stylesheet>
