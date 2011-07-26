/**
 * This file is part of the Iritgo/Aktera Framework.
 *
 * Copyright (C) 2005-2011 Iritgo Technologies.
 * Copyright (C) 2003-2005 BueroByte GbR.
 *
 * Iritgo licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.iritgo.aktera.base.tools;


import java.util.HashMap;
import java.util.Map;


/**
 * Useful methods for locale specific tasks.
 */
public final class LocaleTools
{
	/** Country names (resource keys) by iso country code. */
	protected static Map countryNamesByIsoCode;

	static
	{
		countryNamesByIsoCode = new HashMap ();

		countryNamesByIsoCode.put ("AW", "$1.country.AW");
		countryNamesByIsoCode.put ("AZ", "$1.country.AZ");
		countryNamesByIsoCode.put ("ET", "$1.country.ET");
		countryNamesByIsoCode.put ("AU", "$1.country.AU");
		countryNamesByIsoCode.put ("BS", "$1.country.BS");
		countryNamesByIsoCode.put ("BH", "$1.country.BH");
		countryNamesByIsoCode.put ("BD", "$1.country.BD");
		countryNamesByIsoCode.put ("BB", "$1.country.BB");
		countryNamesByIsoCode.put ("BE", "$1.country.BE");
		countryNamesByIsoCode.put ("BZ", "$1.country.BZ");
		countryNamesByIsoCode.put ("BJ", "$1.country.BJ");
		countryNamesByIsoCode.put ("BM", "$1.country.BM");
		countryNamesByIsoCode.put ("BT", "$1.country.BT");
		countryNamesByIsoCode.put ("MM", "$1.country.MM");
		countryNamesByIsoCode.put ("BO", "$1.country.BO");
		countryNamesByIsoCode.put ("BA", "$1.country.BA");
		countryNamesByIsoCode.put ("BW", "$1.country.BW");
		countryNamesByIsoCode.put ("BV", "$1.country.BV");
		countryNamesByIsoCode.put ("BR", "$1.country.BR");
		countryNamesByIsoCode.put ("IO", "$1.country.IO");
		countryNamesByIsoCode.put ("BN", "$1.country.BN");
		countryNamesByIsoCode.put ("BG", "$1.country.BG");
		countryNamesByIsoCode.put ("BF", "$1.country.BF");
		countryNamesByIsoCode.put ("BI", "$1.country.BI");
		countryNamesByIsoCode.put ("CL", "$1.country.CL");
		countryNamesByIsoCode.put ("CN", "$1.country.CN");
		countryNamesByIsoCode.put ("CX", "$1.country.CX");
		countryNamesByIsoCode.put ("CK", "$1.country.CK");
		countryNamesByIsoCode.put ("CR", "$1.country.CR");
		countryNamesByIsoCode.put ("DK", "$1.country.DK");
		countryNamesByIsoCode.put ("DE", "$1.country.DE");
		countryNamesByIsoCode.put ("DJ", "$1.country.DJ");
		countryNamesByIsoCode.put ("DM", "$1.country.DM");
		countryNamesByIsoCode.put ("DO", "$1.country.DO");
		countryNamesByIsoCode.put ("EC", "$1.country.EC");
		countryNamesByIsoCode.put ("SV", "$1.country.SV");
		countryNamesByIsoCode.put ("CI", "$1.country.CI");
		countryNamesByIsoCode.put ("ER", "$1.country.ER");
		countryNamesByIsoCode.put ("EE", "$1.country.EE");
		countryNamesByIsoCode.put ("FK", "$1.country.FK");
		countryNamesByIsoCode.put ("FO", "$1.country.FO");
		countryNamesByIsoCode.put ("FJ", "$1.country.FJ");
		countryNamesByIsoCode.put ("FI", "$1.country.FI");
		countryNamesByIsoCode.put ("FR", "$1.country.FR");
		countryNamesByIsoCode.put ("GF", "$1.country.GF");
		countryNamesByIsoCode.put ("PF", "$1.country.PF");
		countryNamesByIsoCode.put ("TF", "$1.country.TF");
		countryNamesByIsoCode.put ("GA", "$1.country.GA");
		countryNamesByIsoCode.put ("GM", "$1.country.GM");
		countryNamesByIsoCode.put ("GE", "$1.country.GE");
		countryNamesByIsoCode.put ("GH", "$1.country.GH");
		countryNamesByIsoCode.put ("GI", "$1.country.GI");
		countryNamesByIsoCode.put ("GD", "$1.country.GD");
		countryNamesByIsoCode.put ("GR", "$1.country.GR");
		countryNamesByIsoCode.put ("GL", "$1.country.GL");
		countryNamesByIsoCode.put ("GB", "$1.country.GB");
		countryNamesByIsoCode.put ("GP", "$1.country.GP");
		countryNamesByIsoCode.put ("GU", "$1.country.GU");
		countryNamesByIsoCode.put ("GT", "$1.country.GT");
		countryNamesByIsoCode.put ("GN", "$1.country.GN");
		countryNamesByIsoCode.put ("GW", "$1.country.GW");
		countryNamesByIsoCode.put ("GY", "$1.country.GY");
		countryNamesByIsoCode.put ("HT", "$1.country.HT");
		countryNamesByIsoCode.put ("HM", "$1.country.HM");
		countryNamesByIsoCode.put ("HN", "$1.country.HN");
		countryNamesByIsoCode.put ("HK", "$1.country.HK");
		countryNamesByIsoCode.put ("IN", "$1.country.IN");
		countryNamesByIsoCode.put ("ID", "$1.country.ID");
		countryNamesByIsoCode.put ("IQ", "$1.country.IQ");
		countryNamesByIsoCode.put ("IR", "$1.country.IR");
		countryNamesByIsoCode.put ("IE", "$1.country.IE");
		countryNamesByIsoCode.put ("IS", "$1.country.IS");
		countryNamesByIsoCode.put ("IL", "$1.country.IL");
		countryNamesByIsoCode.put ("IT", "$1.country.IT");
		countryNamesByIsoCode.put ("JM", "$1.country.JM");
		countryNamesByIsoCode.put ("JP", "$1.country.JP");
		countryNamesByIsoCode.put ("YE", "$1.country.YE");
		countryNamesByIsoCode.put ("JO", "$1.country.JO");
		countryNamesByIsoCode.put ("YU", "$1.country.YU");
		countryNamesByIsoCode.put ("KY", "$1.country.KY");
		countryNamesByIsoCode.put ("KH", "$1.country.KH");
		countryNamesByIsoCode.put ("CM", "$1.country.CM");
		countryNamesByIsoCode.put ("CA", "$1.country.CA");
		countryNamesByIsoCode.put ("CV", "$1.country.CV");
		countryNamesByIsoCode.put ("KZ", "$1.country.KZ");
		countryNamesByIsoCode.put ("KE", "$1.country.KE");
		countryNamesByIsoCode.put ("KG", "$1.country.KG");
		countryNamesByIsoCode.put ("KI", "$1.country.KI");
		countryNamesByIsoCode.put ("CC", "$1.country.CC");
		countryNamesByIsoCode.put ("CO", "$1.country.CO");
		countryNamesByIsoCode.put ("KM", "$1.country.KM");
		countryNamesByIsoCode.put ("CG", "$1.country.CG");
		countryNamesByIsoCode.put ("CD", "$1.country.CD");
		countryNamesByIsoCode.put ("HR", "$1.country.HR");
		countryNamesByIsoCode.put ("CU", "$1.country.CU");
		countryNamesByIsoCode.put ("KW", "$1.country.KW");
		countryNamesByIsoCode.put ("LA", "$1.country.LA");
		countryNamesByIsoCode.put ("LS", "$1.country.LS");
		countryNamesByIsoCode.put ("LV", "$1.country.LV");
		countryNamesByIsoCode.put ("LB", "$1.country.LB");
		countryNamesByIsoCode.put ("LR", "$1.country.LR");
		countryNamesByIsoCode.put ("LY", "$1.country.LY");
		countryNamesByIsoCode.put ("LI", "$1.country.LI");
		countryNamesByIsoCode.put ("LT", "$1.country.LT");
		countryNamesByIsoCode.put ("LU", "$1.country.LU");
		countryNamesByIsoCode.put ("MO", "$1.country.MO");
		countryNamesByIsoCode.put ("MG", "$1.country.MG");
		countryNamesByIsoCode.put ("MW", "$1.country.MW");
		countryNamesByIsoCode.put ("MY", "$1.country.MY");
		countryNamesByIsoCode.put ("MV", "$1.country.MV");
		countryNamesByIsoCode.put ("ML", "$1.country.ML");
		countryNamesByIsoCode.put ("MT", "$1.country.MT");
		countryNamesByIsoCode.put ("MP", "$1.country.MP");
		countryNamesByIsoCode.put ("MA", "$1.country.MA");
		countryNamesByIsoCode.put ("MH", "$1.country.MH");
		countryNamesByIsoCode.put ("MQ", "$1.country.MQ");
		countryNamesByIsoCode.put ("MR", "$1.country.MR");
		countryNamesByIsoCode.put ("MU", "$1.country.MU");
		countryNamesByIsoCode.put ("YT", "$1.country.YT");
		countryNamesByIsoCode.put ("MK", "$1.country.MK");
		countryNamesByIsoCode.put ("MX", "$1.country.MX");
		countryNamesByIsoCode.put ("FM", "$1.country.FM");
		countryNamesByIsoCode.put ("MZ", "$1.country.MZ");
		countryNamesByIsoCode.put ("MD", "$1.country.MD");
		countryNamesByIsoCode.put ("MC", "$1.country.MC");
		countryNamesByIsoCode.put ("MN", "$1.country.MN");
		countryNamesByIsoCode.put ("MS", "$1.country.MS");
		countryNamesByIsoCode.put ("NA", "$1.country.NA");
		countryNamesByIsoCode.put ("NR", "$1.country.NR");
		countryNamesByIsoCode.put ("NP", "$1.country.NP");
		countryNamesByIsoCode.put ("NC", "$1.country.NC");
		countryNamesByIsoCode.put ("NZ", "$1.country.NZ");
		countryNamesByIsoCode.put ("NI", "$1.country.NI");
		countryNamesByIsoCode.put ("NL", "$1.country.NL");
		countryNamesByIsoCode.put ("AN", "$1.country.AN");
		countryNamesByIsoCode.put ("NE", "$1.country.NE");
		countryNamesByIsoCode.put ("NG", "$1.country.NG");
		countryNamesByIsoCode.put ("NU", "$1.country.NU");
		countryNamesByIsoCode.put ("KP", "$1.country.KP");
		countryNamesByIsoCode.put ("NF", "$1.country.NF");
		countryNamesByIsoCode.put ("NO", "$1.country.NO");
		countryNamesByIsoCode.put ("OM", "$1.country.OM");
		countryNamesByIsoCode.put ("AT", "$1.country.AT");
		countryNamesByIsoCode.put ("PK", "$1.country.PK");
		countryNamesByIsoCode.put ("PS", "$1.country.PS");
		countryNamesByIsoCode.put ("PW", "$1.country.PW");
		countryNamesByIsoCode.put ("PA", "$1.country.PA");
		countryNamesByIsoCode.put ("PG", "$1.country.PG");
		countryNamesByIsoCode.put ("PY", "$1.country.PY");
		countryNamesByIsoCode.put ("PE", "$1.country.PE");
		countryNamesByIsoCode.put ("PH", "$1.country.PH");
		countryNamesByIsoCode.put ("PN", "$1.country.PN");
		countryNamesByIsoCode.put ("PL", "$1.country.PL");
		countryNamesByIsoCode.put ("PT", "$1.country.PT");
		countryNamesByIsoCode.put ("PR", "$1.country.PR");
		countryNamesByIsoCode.put ("QA", "$1.country.QA");
		countryNamesByIsoCode.put ("RE", "$1.country.RE");
		countryNamesByIsoCode.put ("RW", "$1.country.RW");
		countryNamesByIsoCode.put ("RO", "$1.country.RO");
		countryNamesByIsoCode.put ("RU", "$1.country.RU");
		countryNamesByIsoCode.put ("LC", "$1.country.LC");
		countryNamesByIsoCode.put ("ZM", "$1.country.ZM");
		countryNamesByIsoCode.put ("AS", "$1.country.AS");
		countryNamesByIsoCode.put ("WS", "$1.country.WS");
		countryNamesByIsoCode.put ("SM", "$1.country.SM");
		countryNamesByIsoCode.put ("ST", "$1.country.ST");
		countryNamesByIsoCode.put ("SA", "$1.country.SA");
		countryNamesByIsoCode.put ("SE", "$1.country.SE");
		countryNamesByIsoCode.put ("CH", "$1.country.CH");
		countryNamesByIsoCode.put ("SN", "$1.country.SN");
		countryNamesByIsoCode.put ("SC", "$1.country.SC");
		countryNamesByIsoCode.put ("SL", "$1.country.SL");
		countryNamesByIsoCode.put ("SG", "$1.country.SG");
		countryNamesByIsoCode.put ("SK", "$1.country.SK");
		countryNamesByIsoCode.put ("SI", "$1.country.SI");
		countryNamesByIsoCode.put ("SB", "$1.country.SB");
		countryNamesByIsoCode.put ("SO", "$1.country.SO");
		countryNamesByIsoCode.put ("GS", "$1.country.GS");
		countryNamesByIsoCode.put ("ES", "$1.country.ES");
		countryNamesByIsoCode.put ("LK", "$1.country.LK");
		countryNamesByIsoCode.put ("SH", "$1.country.SH");
		countryNamesByIsoCode.put ("KN", "$1.country.KN");
		countryNamesByIsoCode.put ("PM", "$1.country.PM");
		countryNamesByIsoCode.put ("VC", "$1.country.VC");
		countryNamesByIsoCode.put ("KR", "$1.country.KR");
		countryNamesByIsoCode.put ("ZA", "$1.country.ZA");
		countryNamesByIsoCode.put ("SD", "$1.country.SD");
		countryNamesByIsoCode.put ("SR", "$1.country.SR");
		countryNamesByIsoCode.put ("SJ", "$1.country.SJ");
		countryNamesByIsoCode.put ("SZ", "$1.country.SZ");
		countryNamesByIsoCode.put ("SY", "$1.country.SY");
		countryNamesByIsoCode.put ("TJ", "$1.country.TJ");
		countryNamesByIsoCode.put ("TW", "$1.country.TW");
		countryNamesByIsoCode.put ("TZ", "$1.country.TZ");
		countryNamesByIsoCode.put ("TH", "$1.country.TH");
		countryNamesByIsoCode.put ("TP", "$1.country.TP");
		countryNamesByIsoCode.put ("TG", "$1.country.TG");
		countryNamesByIsoCode.put ("TK", "$1.country.TK");
		countryNamesByIsoCode.put ("TO", "$1.country.TO");
		countryNamesByIsoCode.put ("TT", "$1.country.TT");
		countryNamesByIsoCode.put ("TD", "$1.country.TD");
		countryNamesByIsoCode.put ("CZ", "$1.country.CZ");
		countryNamesByIsoCode.put ("TN", "$1.country.TN");
		countryNamesByIsoCode.put ("TR", "$1.country.TR");
		countryNamesByIsoCode.put ("TM", "$1.country.TM");
		countryNamesByIsoCode.put ("TC", "$1.country.TC");
		countryNamesByIsoCode.put ("TV", "$1.country.TV");
		countryNamesByIsoCode.put ("UG", "$1.country.UG");
		countryNamesByIsoCode.put ("UA", "$1.country.UA");
		countryNamesByIsoCode.put ("HU", "$1.country.HU");
		countryNamesByIsoCode.put ("UY", "$1.country.UY");
		countryNamesByIsoCode.put ("UZ", "$1.country.UZ");
		countryNamesByIsoCode.put ("VU", "$1.country.VU");
		countryNamesByIsoCode.put ("VA", "$1.country.VA");
		countryNamesByIsoCode.put ("VE", "$1.country.VE");
		countryNamesByIsoCode.put ("AE", "$1.country.AE");
		countryNamesByIsoCode.put ("US", "$1.country.US");
		countryNamesByIsoCode.put ("VN", "$1.country.VN");
		countryNamesByIsoCode.put ("VG", "$1.country.VG");
		countryNamesByIsoCode.put ("VI", "$1.country.VI");
		countryNamesByIsoCode.put ("WF", "$1.country.WF");
		countryNamesByIsoCode.put ("BY", "$1.country.BY");
		countryNamesByIsoCode.put ("EH", "$1.country.EH");
		countryNamesByIsoCode.put ("CF", "$1.country.CF");
		countryNamesByIsoCode.put ("ZW", "$1.country.ZW");
		countryNamesByIsoCode.put ("CY", "$1.country.CY");
	}

	/**
	 * Get a country name (resource key) by it's iso country code.
	 *
	 * @param isoCode The iso country code.
	 * @return The country name.
	 */
	public static String getCountryNameByIsoCode (String isoCode)
	{
		return (String) countryNamesByIsoCode.get (isoCode);
	}
}
