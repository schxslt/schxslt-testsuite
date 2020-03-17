<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:param name="phase"/>
  <xsl:template match="/">
    <output phase="{$phase}"/>
  </xsl:template>
</xsl:transform>
