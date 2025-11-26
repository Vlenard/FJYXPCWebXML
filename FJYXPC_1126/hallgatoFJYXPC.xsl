<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- Output formátum XML -->
    <xsl:output method="xml" indent="yes"/>

    <!-- Gyökérelem -->
    <xsl:template match="/class">
        <studentsTable>
            <xsl:for-each select="student">
                <studentRecord>
                    <id>
                        <xsl:value-of select="@id"/>
                    </id>
                    <vezeteknev>
                        <xsl:value-of select="vezeteknev"/>
                    </vezeteknev>
                    <keresztnev>
                        <xsl:value-of select="keresztnev"/>
                    </keresztnev>
                    <becenev>
                        <xsl:value-of select="becenev"/>
                    </becenev>
                    <kor>
                        <xsl:value-of select="kor"/>
                    </kor>
                    <ossztondij>
                        <xsl:value-of select="ossztondij"/>
                    </ossztondij>
                </studentRecord>
            </xsl:for-each>
        </studentsTable>
    </xsl:template>

</xsl:stylesheet>
