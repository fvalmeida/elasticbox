/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package temp.org.apache.tika.example;

import org.apache.tika.Tika;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class SimpleTypeDetector {

    public static void main(String[] args) throws Exception {
        Tika tika = new Tika();

        args = new String[]{
                "/Users/fvalmeida/Downloads/0001461418.pdf", "/Users/fvalmeida/Downloads/03052013.11", "/Users/fvalmeida/Downloads/11_622_Bonus_track.1.mp3", "/Users/fvalmeida/Downloads/2+bicho+maior.wav", "/Users/fvalmeida/Downloads/20111104-trello.jpg", "/Users/fvalmeida/Downloads/234936320-Binary-Security-Tokeb-Tib.pdf", "/Users/fvalmeida/Downloads/4800001448_0.xml", "/Users/fvalmeida/Downloads/56631191-Understanding-WSS-2009.doc", "/Users/fvalmeida/Downloads/A Thousand Years (Alto Sax).pdf", "/Users/fvalmeida/Downloads/AdobeFlashPlayer_18au_a_install.dmg", "/Users/fvalmeida/Downloads/AntiWPA", "/Users/fvalmeida/Downloads/ApresentaçãoTCC - Revisado.ppt", "/Users/fvalmeida/Downloads/ApresentaçãoTCC_210115_2.ppt", "/Users/fvalmeida/Downloads/ApresentaçãoTCC_revisado.ppt", "/Users/fvalmeida/Downloads/BAPI_MATERIAL_SAVEDATA_PROJETO_P-76_14_01_2014_Rev_2.xlsx", "/Users/fvalmeida/Downloads/Brackets.1.1.Extract.dmg", "/Users/fvalmeida/Downloads/Brandon Grotesque", "/Users/fvalmeida/Downloads/BraslogMaterialDTO_07242015_095526283.xlsx", "/Users/fvalmeida/Downloads/CCMacSetup109.dmg", "/Users/fvalmeida/Downloads/Captura_de_Tela_2012-06-12__s_07.48.png", "/Users/fvalmeida/Downloads/Carbon Copy Cloner.dmg", "/Users/fvalmeida/Downloads/Casa Nova RJ.zip", "/Users/fvalmeida/Downloads/Certificados Felipe.zip", "/Users/fvalmeida/Downloads/Certificados.zip", "/Users/fvalmeida/Downloads/CitrixReceiver11.9.15.dmg", "/Users/fvalmeida/Downloads/CitrixReceiverWeb.dmg", "/Users/fvalmeida/Downloads/CoRD_0.5.7.zip", "/Users/fvalmeida/Downloads/DISPLAX SKIN MULTITOUCH Spec File_MKT.155.18.pdf", "/Users/fvalmeida/Downloads/DashboardFiscal - BR13 - 0001.dxp", "/Users/fvalmeida/Downloads/Deeper.dmg", "/Users/fvalmeida/Downloads/EF - Elle Nkumane", "/Users/fvalmeida/Downloads/ERRO SAP.docx", "/Users/fvalmeida/Downloads/EmpresasNet.zip", "/Users/fvalmeida/Downloads/EpcplanServicesP76.rar", "/Users/fvalmeida/Downloads/Essential-Guide-to-API-Monitoring-October-2014.pdf", "/Users/fvalmeida/Downloads/FabricIntelliJPlugin.zip", "/Users/fvalmeida/Downloads/Folder (1).docx", "/Users/fvalmeida/Downloads/Folder.docx", "/Users/fvalmeida/Downloads/Folder_Namorados (1).docx", "/Users/fvalmeida/Downloads/Folder_Namorados.docx", "/Users/fvalmeida/Downloads/FoxitReader715.0425_prom_enu_Setup.exe", "/Users/fvalmeida/Downloads/Gems4.1.zip", "/Users/fvalmeida/Downloads/IGA - Aros de cebola fritos.m4a", "/Users/fvalmeida/Downloads/IGA.m4a", "/Users/fvalmeida/Downloads/IMG-20150412-WA0000.jpg", "/Users/fvalmeida/Downloads/IMG-20150415-WA0001.jpg", "/Users/fvalmeida/Downloads/IRPF2015macOS-Xv1.2.app.tar.gz", "/Users/fvalmeida/Downloads/Inkscape-0.91-1-x11-10.7-x86_64.dmg", "/Users/fvalmeida/Downloads/Instrutivo Cadastrar Oportunidade de Inovação.doc", "/Users/fvalmeida/Downloads/IntelliJIDEA_ReferenceCard_Mac.pdf", "/Users/fvalmeida/Downloads/JRT.exe", "/Users/fvalmeida/Downloads/Joomla_3.3.6-Stable-Full_Package.zip", "/Users/fvalmeida/Downloads/LyncSetup.exe", "/Users/fvalmeida/Downloads/MAMEOSX-0.135.dmg", "/Users/fvalmeida/Downloads/MAS-Checksheet-Rev-Sept-2014.pdf", "/Users/fvalmeida/Downloads/MAS-Course-Descriptions-2015-03-31 (1).pdf", "/Users/fvalmeida/Downloads/MAS-Course-Descriptions-2015-03-31.pdf", "/Users/fvalmeida/Downloads/MODELO MEMORIAL DESCRITIVO.docx", "/Users/fvalmeida/Downloads/MRR01_Detail_Results.xml", "/Users/fvalmeida/Downloads/MRR_06 fev 2015_TEKFEN.xls", "/Users/fvalmeida/Downloads/Maintenance.dmg", "/Users/fvalmeida/Downloads/Manual del Comité de IT.docx", "/Users/fvalmeida/Downloads/Memory Leak.docx", "/Users/fvalmeida/Downloads/Minhas Atividades - Felipe Vieira Almeida.xlsx", "/Users/fvalmeida/Downloads/NetFx20SP2_x86.exe", "/Users/fvalmeida/Downloads/NorthConcepts-DataPipeline-2.3.3", "/Users/fvalmeida/Downloads/NorthConcepts-DataPipeline-2.3.3.zip", "/Users/fvalmeida/Downloads/NorthConcepts-WebApp-Part2", "/Users/fvalmeida/Downloads/NorthConcepts-WebApp-Part2.zip", "/Users/fvalmeida/Downloads/OnyX.dmg", "/Users/fvalmeida/Downloads/P76-SPMat-Interface_B2_CONSTR_ADD_20150320_152021.zip", "/Users/fvalmeida/Downloads/P76-SPMat-Interface_B2_CONSTR_ADD_20150324_200126.zip", "/Users/fvalmeida/Downloads/P76_SPMAT_CatalogList.csv", "/Users/fvalmeida/Downloads/P76_users_latest_access.xlsx", "/Users/fvalmeida/Downloads/PO_QTY_Problem.xlsx", "/Users/fvalmeida/Downloads/PSTools.zip", "/Users/fvalmeida/Downloads/Paintbrush-2.1.1.zip", "/Users/fvalmeida/Downloads/Parâm_ A5 Evolution - Word.doc", "/Users/fvalmeida/Downloads/Pixelm331.zip", "/Users/fvalmeida/Downloads/Plan Operativo _ Felipe Almeida.xls", "/Users/fvalmeida/Downloads/Procedimiento de Capacity Presupuestado e Incurrido.doc", "/Users/fvalmeida/Downloads/QueryAD.sql", "/Users/fvalmeida/Downloads/REQ_PO_Items_Last_Rev_Status_CVMelo.xls", "/Users/fvalmeida/Downloads/RN_SPMAT_SAP_Mapping_20140530.xlsx", "/Users/fvalmeida/Downloads/Receitanet-1.04.app.tar.gz", "/Users/fvalmeida/Downloads/Receitanet-1.07.app.tar.gz", "/Users/fvalmeida/Downloads/RemoveWGA.exe", "/Users/fvalmeida/Downloads/SAP-Logo.png", "/Users/fvalmeida/Downloads/SAP-Logo.svg", "/Users/fvalmeida/Downloads/SDL-1.2.15.dmg", "/Users/fvalmeida/Downloads/SDL2-2.0.3.dmg", "/Users/fvalmeida/Downloads/SIC - Familias e Atividades.xlsx", "/Users/fvalmeida/Downloads/SQLManagementStudio_x86_PTB.exe", "/Users/fvalmeida/Downloads/SQLServer2005_SSMSEE.msi", "/Users/fvalmeida/Downloads/STATEMENT OF PURPOSE - Hugo.pdf", "/Users/fvalmeida/Downloads/SendToKindleForMac-installer.pkg", "/Users/fvalmeida/Downloads/Skype_hidden_Emoticon_headbang.gif", "/Users/fvalmeida/Downloads/SoapUI-5.1.2.dmg", "/Users/fvalmeida/Downloads/Soundflower.zip", "/Users/fvalmeida/Downloads/Spools_IdentCode.xlsx", "/Users/fvalmeida/Downloads/Status Sistemas Tebra -  20150123.pptx", "/Users/fvalmeida/Downloads/Sublime Text 2.0.2.dmg", "/Users/fvalmeida/Downloads/Sublime Text Build 3065.dmg", "/Users/fvalmeida/Downloads/Sublime Text Build 3083.dmg", "/Users/fvalmeida/Downloads/TECD_apresentação.pdf", "/Users/fvalmeida/Downloads/TIB_BW_6.2.1_HF-002_readme.txt", "/Users/fvalmeida/Downloads/TIB_BW_6.2.1_macosx_x86_64.zip", "/Users/fvalmeida/Downloads/TIB_BW_administration.pdf", "/Users/fvalmeida/Downloads/TIB_rtview_ems_6.3.0.zip", "/Users/fvalmeida/Downloads/TIB_rtview_ems_6.3.0_documents.zip", "/Users/fvalmeida/Downloads/Takipi (1).dmg", "/Users/fvalmeida/Downloads/Takipi.dmg", "/Users/fvalmeida/Downloads/TesseractGUI.zip", "/Users/fvalmeida/Downloads/TuneUpUtilities2014_en-US.exe", "/Users/fvalmeida/Downloads/VisualVM_138.dmg", "/Users/fvalmeida/Downloads/WindowsXP-KB926140-v5-x86-PTB.exe", "/Users/fvalmeida/Downloads/Wineskin Winery.app Version 1.7.zip", "/Users/fvalmeida/Downloads/Workbook1 (version 1).xlsx", "/Users/fvalmeida/Downloads/XQuartz-2.7.7.dmg", "/Users/fvalmeida/Downloads/XsdToSql.zip", "/Users/fvalmeida/Downloads/Yanni - Play Time.mp3", "/Users/fvalmeida/Downloads/adwcleaner_4.112.exe", "/Users/fvalmeida/Downloads/airdroid_desktop_client_3.0.1.dmg", "/Users/fvalmeida/Downloads/anyconnect-macosx-i386-3.1.06073-k9.dmg", "/Users/fvalmeida/Downloads/bapi code.doc", "/Users/fvalmeida/Downloads/ccc-4.0.3.3847.zip", "/Users/fvalmeida/Downloads/classificacao.doc", "/Users/fvalmeida/Downloads/client.chm", "/Users/fvalmeida/Downloads/client.zip", "/Users/fvalmeida/Downloads/cm-11-20141021-NIGHTLY-d2lte.zip", "/Users/fvalmeida/Downloads/com.google.android.tts-3.4.6.1819666.x86-210304061-minAPI15.apk", "/Users/fvalmeida/Downloads/consorcio_p76_oficial_title.png", "/Users/fvalmeida/Downloads/consorcio_p76_oficial_title_.png", "/Users/fvalmeida/Downloads/cports-x64.zip", "/Users/fvalmeida/Downloads/cu.xml", "/Users/fvalmeida/Downloads/cuci-lync-Install-ffr.8-6-2", "/Users/fvalmeida/Downloads/ecc-04.jpg", "/Users/fvalmeida/Downloads/elasticsearch-1.7.0", "/Users/fvalmeida/Downloads/elasticsearch-1.7.0.zip", "/Users/fvalmeida/Downloads/feedly.opml", "/Users/fvalmeida/Downloads/font-awesome-4.3.0.zip", "/Users/fvalmeida/Downloads/fotoempleado.jpeg", "/Users/fvalmeida/Downloads/gbbd_uni_setup.pkg", "/Users/fvalmeida/Downloads/googlechrome (1).dmg", "/Users/fvalmeida/Downloads/googlechrome.dmg", "/Users/fvalmeida/Downloads/googlesoftwareupdate-1.1.0.3659.dmg", "/Users/fvalmeida/Downloads/guava-concurrent-slides.pdf", "/Users/fvalmeida/Downloads/how_chef_works.mp4", "/Users/fvalmeida/Downloads/iTerm2_v2_0.zip", "/Users/fvalmeida/Downloads/iconworkshop.exe", "/Users/fvalmeida/Downloads/ideaIU-14.1.dmg", "/Users/fvalmeida/Downloads/image001.jpg@01D03198.FA8032C0", "/Users/fvalmeida/Downloads/jdk-7u75-macosx-x64.dmg", "/Users/fvalmeida/Downloads/jdk-7u79-macosx-x64.dmg", "/Users/fvalmeida/Downloads/jprofiler_macos_9_0_2.dmg", "/Users/fvalmeida/Downloads/kdiff3-0.9.98-MacOSX-64Bit.dmg", "/Users/fvalmeida/Downloads/kibana-4.1.1-darwin-x64", "/Users/fvalmeida/Downloads/kibana-4.1.1-darwin-x64.tar.gz", "/Users/fvalmeida/Downloads/kmspico_9.3.1.rar", "/Users/fvalmeida/Downloads/large1.jpg", "/Users/fvalmeida/Downloads/large2.jpg", "/Users/fvalmeida/Downloads/large3.jpg", "/Users/fvalmeida/Downloads/large4.jpg", "/Users/fvalmeida/Downloads/mame0160-64bit.zip", "/Users/fvalmeida/Downloads/mbam-setup-2.0.4.1028.exe", "/Users/fvalmeida/Downloads/microsoft_toolkit.exe", "/Users/fvalmeida/Downloads/node-v0.10.32.pkg", "/Users/fvalmeida/Downloads/node-v0.10.9.pkg", "/Users/fvalmeida/Downloads/node-v0.12.2.pkg", "/Users/fvalmeida/Downloads/ntlmaps-0.9.9.0.1", "/Users/fvalmeida/Downloads/ntlmaps-0.9.9.0.1.zip", "/Users/fvalmeida/Downloads/orange_juice", "/Users/fvalmeida/Downloads/pn6nu.Red.Gate.SQL.Toolbelt.2013.1.8.2.353.rar", "/Users/fvalmeida/Downloads/processactivityview-x64.zip", "/Users/fvalmeida/Downloads/procexp.exe", "/Users/fvalmeida/Downloads/products-WB0B30DGR", "/Users/fvalmeida/Downloads/referencia_sql_server.txt", "/Users/fvalmeida/Downloads/sap_material_template.xlsx", "/Users/fvalmeida/Downloads/soapenv.example.xml", "/Users/fvalmeida/Downloads/telareal.xls", "/Users/fvalmeida/Downloads/telerik.kendoui.professional.2014.2.903.trial.zip", "/Users/fvalmeida/Downloads/telerik.ui.for.jsp.2014.2.903.trial.zip", "/Users/fvalmeida/Downloads/tesseract-ocr", "/Users/fvalmeida/Downloads/tesseract-ocr-3.02.eng.tar.gz", "/Users/fvalmeida/Downloads/tibco problem.docx", "/Users/fvalmeida/Downloads/tnsnames.ora", "/Users/fvalmeida/Downloads/webplayer-mini.dmg", "/Users/fvalmeida/Downloads/wildfly-8.2.0.Final.zip", "/Users/fvalmeida/Downloads/workspace.zip", "/Users/fvalmeida/Downloads/xmltask.jar"
        };

        for (String file : args) {
            File file1 = new File(file);
            System.out.println(file + ": " + tika.detect(file1));
            BasicFileAttributes attr = Files.readAttributes(file1.toPath(), BasicFileAttributes.class);
            System.out.println("creationTime: " + attr.creationTime());
            System.out.println("lastAccessTime: " + attr.lastAccessTime());
            System.out.println("lastModifiedTime: " + attr.lastModifiedTime());
            System.out.println();
        }
    }

}
