<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
				xmlns:xalan="http://xml.apache.org/xalan">

<xsl:output method="xml" encoding="utf-8" indent="yes" xalan:indent-amount="5"/>


<xsl:template name="globalReplace">
	<xsl:param name="outputString"/>
	<xsl:param name="target"/>
	<xsl:param name="replacement"/>
	<xsl:choose>
		<xsl:when test="contains($outputString,$target)">
			<xsl:value-of select="concat(substring-before($outputString,$target),$replacement)"/>
			<xsl:call-template name="globalReplace">
				<xsl:with-param name="outputString" select="substring-after($outputString,$target)"/>
				<xsl:with-param name="target" select="$target"/>
				<xsl:with-param name="replacement" select="$replacement"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$outputString"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>


<xsl:template name="setSalutation">
	<xsl:param name="outputString"/>
	<xsl:choose>
		<xsl:when test="contains($outputString,'Frau')">
			<xsl:value-of select="concat(substring-before($outputString,'Frau'),'mrs')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Herr')">
			<xsl:value-of select="concat(substring-before($outputString,'Herr'),'mr')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Firma')">
			<xsl:value-of select="concat(substring-before($outputString,'Firma'),'firm')"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$outputString"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>


<xsl:template name="setCountry">
	<xsl:param name="outputString"/>
	<xsl:choose>
		<xsl:when test="contains($outputString,'Deutschland')">
			<xsl:value-of select="concat(substring-before($outputString,'Deutschland'),'DE')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'BRD')">
			<xsl:value-of select="concat(substring-before($outputString,'BRD'),'DE')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Germany')">
			<xsl:value-of select="concat(substring-before($outputString,'Germany'),'DE')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Andorra')">
			<xsl:value-of select="concat(substring-before($outputString,'Andorra'),'AD')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Albanien')">
			<xsl:value-of select="concat(substring-before($outputString,'Albanien'),'AL')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Armenien')">
			<xsl:value-of select="concat(substring-before($outputString,'Armenien'),'AM')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Albanien')">
			<xsl:value-of select="concat(substring-before($outputString,'Albanien'),'AL')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Argentinien')">
			<xsl:value-of select="concat(substring-before($outputString,'Argentinien'),'AR')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Österreich')">
			<xsl:value-of select="concat(substring-before($outputString,'Österreich'),'AT')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Austria')">
			<xsl:value-of select="concat(substring-before($outputString,'Austria'),'AT')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Australien')">
			<xsl:value-of select="concat(substring-before($outputString,'Australien'),'AU')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Bosnien-Herzegovina')">
			<xsl:value-of select="concat(substring-before($outputString,'Bosnien-Herzegovina'),'BA')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Belgien')">
			<xsl:value-of select="concat(substring-before($outputString,'Belgien'),'BE')"/>
		</xsl:when>
			<xsl:when test="contains($outputString,'Bulgarien')">
		<xsl:value-of select="concat(substring-before($outputString,'Bulgarien'),'BG')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Weißrußland')">
			<xsl:value-of select="concat(substring-before($outputString,'Bulgarien'),'BY')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Canada')">
			<xsl:value-of select="concat(substring-before($outputString,'Canada'),'CA')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Schweiz')">
			<xsl:value-of select="concat(substring-before($outputString,'Schweiz'),'CH')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'China')">
			<xsl:value-of select="concat(substring-before($outputString,'China'),'CN')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Zypern')">
			<xsl:value-of select="concat(substring-before($outputString,'Zypern'),'CY')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Tschechische Republik')">
			<xsl:value-of select="concat(substring-before($outputString,'Tschechische Republik'),'CZ')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Dänemark')">
			<xsl:value-of select="concat(substring-before($outputString,'Dänemark'),'DK')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Algerien')">
			<xsl:value-of select="concat(substring-before($outputString,'Algerien'),'DZ')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Spanien')">
			<xsl:value-of select="concat(substring-before($outputString,'Spanien'),'ES')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Finnland')">
			<xsl:value-of select="concat(substring-before($outputString,'Finnland'),'FI')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Frankreich')">
			<xsl:value-of select="concat(substring-before($outputString,'Frankreich'),'FR')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Großbritannien')">
			<xsl:value-of select="concat(substring-before($outputString,'Großbritannien'),'GB')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'England')">
			<xsl:value-of select="concat(substring-before($outputString,'England'),'GB')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Griechenland')">
			<xsl:value-of select="concat(substring-before($outputString,'Griechenland'),'GR')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Hong Kong')">
			<xsl:value-of select="concat(substring-before($outputString,'Hong Kong'),'HK')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Kroatien')">
			<xsl:value-of select="concat(substring-before($outputString,'Kroatien'),'HR')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Ungarn')">
			<xsl:value-of select="concat(substring-before($outputString,'Ungarn'),'HU')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Irland')">
			<xsl:value-of select="concat(substring-before($outputString,'Irland'),'IE')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Israel')">
			<xsl:value-of select="concat(substring-before($outputString,'Israel'),'IL')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Indien')">
			<xsl:value-of select="concat(substring-before($outputString,'Indien'),'IN')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Italien')">
			<xsl:value-of select="concat(substring-before($outputString,'Italien'),'IT')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Japan')">
			<xsl:value-of select="concat(substring-before($outputString,'Japan'),'JP')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Japan')">
			<xsl:value-of select="concat(substring-before($outputString,'Japan'),'JP')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Liechtenstein')">
			<xsl:value-of select="concat(substring-before($outputString,'Liechtenstein'),'LI')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Littauen')">
			<xsl:value-of select="concat(substring-before($outputString,'Littauen'),'LT')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Luxemburg')">
			<xsl:value-of select="concat(substring-before($outputString,'Luxemburg'),'LU')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Lettland')">
			<xsl:value-of select="concat(substring-before($outputString,'Lettland'),'LU')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Niederlande')">
			<xsl:value-of select="concat(substring-before($outputString,'Niederlande'),'NL')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Holland')">
			<xsl:value-of select="concat(substring-before($outputString,'Holland'),'NL')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Norwegen')">
			<xsl:value-of select="concat(substring-before($outputString,'Norwegen'),'NO')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Polen')">
			<xsl:value-of select="concat(substring-before($outputString,'Polen'),'PO')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Portugal')">
			<xsl:value-of select="concat(substring-before($outputString,'Portugal'),'PT')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Rumänien')">
			<xsl:value-of select="concat(substring-before($outputString,'Rumänien'),'RU')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Russische Föderation')">
			<xsl:value-of select="concat(substring-before($outputString,'Russische Föderation'),'RU')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Saudi Arabien')">
			<xsl:value-of select="concat(substring-before($outputString,'Saudi Arabien'),'SA')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Schweden')">
			<xsl:value-of select="concat(substring-before($outputString,'Schweden'),'SE')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Slowakei')">
			<xsl:value-of select="concat(substring-before($outputString,'Slowakei'),'SK')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Türkei')">
			<xsl:value-of select="concat(substring-before($outputString,'Türkei'),'TR')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Ukraine')">
			<xsl:value-of select="concat(substring-before($outputString,'Ukraine'),'UA')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'USA')">
			<xsl:value-of select="concat(substring-before($outputString,'USA'),'US')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Vereinigte Staaten')">
			<xsl:value-of select="concat(substring-before($outputString,'Vereinigte Staaten'),'US')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'USA')">
			<xsl:value-of select="concat(substring-before($outputString,'USA'),'US')"/>
		</xsl:when>
		<xsl:when test="contains($outputString,'Jugoslawien')">
			<xsl:value-of select="concat(substring-before($outputString,'Jugoslawien'),'YU')"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$outputString"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>


<xsl:template name="adjustString">
	<xsl:param name="sourceString"/>
	<xsl:variable name="markString1">
		<xsl:call-template name="globalReplace">
			<xsl:with-param name="outputString" select="$sourceString"/>
			<xsl:with-param name="target" select="'ö'"/>
			<xsl:with-param name="replacement" select="'oe'"/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="markString2">
		<xsl:call-template name="globalReplace">
			<xsl:with-param name="outputString" select="$markString1"/>
			<xsl:with-param name="target" select="'ü'"/>
			<xsl:with-param name="replacement" select="'ue'"/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="markString3">
		<xsl:call-template name="globalReplace">
			<xsl:with-param name="outputString" select="$markString2"/>
			<xsl:with-param name="target" select="'ä'"/>
			<xsl:with-param name="replacement" select="'ae'"/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="markString4">
		<xsl:call-template name="globalReplace">
			<xsl:with-param name="outputString" select="$markString3"/>
			<xsl:with-param name="target" select="'ß'"/>
			<xsl:with-param name="replacement" select="'ss'"/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="markString5">
		<xsl:call-template name="globalReplace">
			<xsl:with-param name="outputString" select="$markString4"/>
			<xsl:with-param name="target" select="'Ö'"/>
			<xsl:with-param name="replacement" select="'Oe'"/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="markString6">
		<xsl:call-template name="globalReplace">
			<xsl:with-param name="outputString" select="$markString5"/>
			<xsl:with-param name="target" select="'Ü'"/>
			<xsl:with-param name="replacement" select="'Ue'"/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="markString7">
		<xsl:call-template name="globalReplace">
			<xsl:with-param name="outputString" select="$markString6"/>
			<xsl:with-param name="target" select="'Ä'"/>
			<xsl:with-param name="replacement" select="'Ae'"/>
		</xsl:call-template>
	</xsl:variable>

	<!-- delete wrong spaces in string -->
	<xsl:variable name="markString8">
		<xsl:value-of select="normalize-space($markString7)" />
	</xsl:variable>

	<!-- replace delete "/" -->
	<xsl:variable name="markString9">
		<xsl:value-of select="translate($markString8,'/','')"/>
	</xsl:variable>

	<!-- replace delete "." -->
	<xsl:variable name="markString10">
		<xsl:value-of select="translate($markString9,'.','')"/>
	</xsl:variable>

	<!-- replace regular Space with "_" -->
  <xsl:value-of select="translate($markString10,' ','_')"/>

</xsl:template>


<xsl:template match="/import">

	<xsl:element name="import" namespace="">

		<addresses>

			<xsl:for-each select="row">

				<xsl:element name="address">

					<xsl:attribute name="category">G</xsl:attribute>

                	<salutation>
						<xsl:call-template name="setSalutation">
							<xsl:with-param name="outputString" select="normalize-space(Anrede)"/>
						</xsl:call-template>
                    </salutation>

					<firstName>
					  	<xsl:value-of select="normalize-space(Vorname)"/>
					</firstName>

					<lastName>
						<xsl:value-of select="normalize-space(Nachname)"/>
					</lastName>

					<company>
					  	<xsl:value-of select="normalize-space(Firma)"/>
					</company>

					<division>
					  	<xsl:value-of select="normalize-space(Abteilung)"/>
					</division>

					<position>
					  	<xsl:value-of select="normalize-space(Position)"/>
					</position>

					<street>
					  	<xsl:value-of select="normalize-space(Strasse)"/>
					</street>

					<postalCode>
					  	<xsl:value-of select="normalize-space(PLZ)"/>
					</postalCode>

					<city>
					  	<xsl:value-of select="normalize-space(Stadt)"/>
					</city>

					<country>
						<xsl:call-template name="setCountry">
					 		<xsl:with-param name="outputString" select="normalize-space(Land)"/>
						</xsl:call-template>
                    </country>

					<email>
					  	<xsl:value-of select="normalize-space(eMail)"/>
					</email>

					<web>
					  	<xsl:value-of select="normalize-space(WWW)"/>
					</web>

					<contactNumber>
					  	<xsl:value-of select="normalize-space(KontaktNr)"/>
					</contactNumber>

					<companyNumber>
					  	<xsl:value-of select="normalize-space(KundenNr)"/>
					</companyNumber>

					<phoneNumbers>

    					<phoneNumber category='B'>
							<xsl:value-of select="normalize-space(FestnetzFirma)"/>
						</phoneNumber>

    					<phoneNumber category='BM'>
							<xsl:value-of select="normalize-space(MobilFirma)"/>
						</phoneNumber>

    					<phoneNumber category='BF'>
							<xsl:value-of select="normalize-space(FaxFirma)"/>
						</phoneNumber>

	    				<phoneNumber category='BDD'>
							<xsl:value-of select="normalize-space(DurchwahlFirma)"/>
						</phoneNumber>

    					<phoneNumber category='P'>
							<xsl:value-of select="normalize-space(FestnetzPrivat)"/>
						</phoneNumber>

    					<phoneNumber category='PM'>
							<xsl:value-of select="normalize-space(MobilPrivat)"/>
						</phoneNumber>

    					<phoneNumber category='PF'>
							<xsl:value-of select="normalize-space(FaxPrivat)"/>
						</phoneNumber>

	   	 				<phoneNumber category='VOIP'>
							<xsl:value-of select="normalize-space(VoIP)"/>
						</phoneNumber>

					</phoneNumbers>

					<remark>
						<xsl:value-of select="normalize-space(Bemerkungen)"/>
					</remark>

				</xsl:element>

			</xsl:for-each>

		</addresses>

	</xsl:element>

</xsl:template>

</xsl:stylesheet>
