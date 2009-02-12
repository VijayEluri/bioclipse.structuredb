<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" encoding="UTF-8" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
        doctype-system="http://www.w3.org/TR/html4/loose.dtd" indent="yes"/>

<xsl:template match="/">
<html>
<head>
  <title>Extension Point '<xsl:value-of select="/list/plugin/extension/@point"/>' in Bioclipse</title>
  <style type="text/css">                                          
     body { font:normal verdana,arial,helvetica; color:#000000; }
     table { margin-left: 5%; }
  </style>
</head>
<body>
<h1>Extension Point '<xsl:value-of select="/list/plugin/extension/@point"/>' in Bioclipse</h1>
<xsl:for-each select="/list/plugin[./extension]">
  <xsl:apply-templates select="."/>
</xsl:for-each>
</body>
</html>
</xsl:template>

<xsl:template match="plugin">
  <xsl:param name="plugin" select="./@id"/>
  <xsl:for-each select="./extension/content-type">
    <xsl:apply-templates select=".">
      <xsl:with-param name="plugin" select="$plugin"/>
    </xsl:apply-templates>
  </xsl:for-each>
</xsl:template>

<xsl:template match="content-type">
  <xsl:param name="plugin"/>
<h2><xsl:element name="a">
<xsl:attribute name="name"><xsl:value-of select="./@id"/></xsl:attribute>
<xsl:value-of select="./@name"/></xsl:element></h2>
<table>
<tr><td><b>plugin</b></td><td><xsl:element name="a">
<xsl:attribute name="href">http://bioclipse.svn.sourceforge.net/viewvc/bioclipse/bioclipse2/trunk/plugins/<xsl:value-of select="$plugin"/></xsl:attribute>
<xsl:value-of select="$plugin"/></xsl:element></td></tr>
<tr><td><b>parent</b></td><td><xsl:value-of select="./@base-type"/></td></tr>
<tr><td><b>id</b></td><td><xsl:value-of select="./@id"/></td></tr>
<xsl:if test="./describer">
<tr><td><b>describer</b></td><td><xsl:value-of select="./describer/@class"/></td></tr>
  <xsl:if test="./describer/parameter">
    <xsl:for-each select="./describer//parameter">
      <tr><td></td><td><i><xsl:value-of select="./@name"/></i>: <xsl:value-of select="./@value"/></td></tr>
    </xsl:for-each>
  </xsl:if>
</xsl:if>
<xsl:if test="./@file-extensions">
<tr><td><b>extensions</b></td><td><xsl:value-of select="./@file-extensions"/></td></tr>
</xsl:if>
</table>
</xsl:template>

</xsl:stylesheet>