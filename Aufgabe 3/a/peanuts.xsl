<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">

<html>
    
<head>

    <style>
        
        th, td {
            padding: 3px 7px;
            text-align: left;
            font-size: 14px;
        }
        
        th {
            background-color: #ffc137;
            font-weight: normal;
            font-size: 16px;
        }
        
        td {
            background-color: #ffe4a8;
        }
    
    </style>
    
</head>    

<body>

  <h2>Darsteller der Peanuts</h2>

  <table border="0" cellspacing="0" cellpadding="0">
      
    <tr>
      <th style="text-align: left;">Vorname</th>
      <th style="text-align: left;">Name</th>
      <th style="text-align: left;">Einführungsjahr</th>
    </tr>
	
    <xsl:for-each select="peanuts/character">
      <xsl:sort select="surname"/>
      <tr>
        <td><xsl:value-of select="name"/></td>
        <td><xsl:value-of select="surname"/></td>
        <td><xsl:value-of select="@intro"/></td>
      </tr>
    </xsl:for-each>
  
  </table>

</body>
</html>

</xsl:template>
</xsl:stylesheet>