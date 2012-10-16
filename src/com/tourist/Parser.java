package com.tourist;

public class Parser {
	
	public int countRows(String strWS)
	{
		int iRows = 0;
		if(strWS.startsWith("~"))//string sa web servisa mora da pocinje sa znakom '~'
		{
			String[] splitedString = strWS.split( "~" );
			iRows = splitedString.length - 1;
		}
		else
		{
			return -1;
		}
		
		return iRows;
	}
	
	public int countCols(String strWS)
	{
		
		int iCols = 0;
		if(strWS.startsWith("~"))//string sa web servisa mora da pocinje sa znakom '~'
		{
			//iz stringa odvajamo samo prvi red
			int iSecondTilda = strWS.indexOf("~", 1);
			String firstRow = strWS.substring(0, iSecondTilda);
			//i racunamo broj polja
			String[] splitedString = firstRow.split( "," );
			iCols = splitedString.length - 1;
		}
		else
		{
			return -1;
		}
		
		return iCols;
	}
	
	public String parseData(String strWS, int iRow, int iCol)
	{
		String strResult = "";
		/*
		if(iRow>=countRows(strWS))
		{
			return "Zadati broj reda premasuje!";
		}
		
		if(iCol>=countCols(strWS))
		{
			return "Zadati broj kolone premasuje!";
		}
		*/
		if(strWS.startsWith("~"))//string sa web servisa mora da pocinje sa znakom '~'
		{
			String[] arrRows = strWS.split( "~" );//ceo string parsujemo po tildama
			String rowTarget = arrRows[iRow+1]; // izdvojen je zeljeni red
			
			String[] arrCols = rowTarget.split( "," );// izdvojeni red parsujemo po zarezima
			String colTarget = arrCols[iCol];// izdvajamo zeljeno polje
			
			String[] arrData = colTarget.split( "\"" );
			String dataTarget = arrData[1];
			
			strResult = dataTarget;
		}
		else
		{
			return "-1";
		}
		return strResult;
	}
}
