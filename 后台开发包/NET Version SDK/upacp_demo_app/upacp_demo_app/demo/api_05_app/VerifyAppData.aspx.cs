using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using com.unionpay.acp.sdk;
using System.Text;
using System.IO;

namespace upacp_demo_app.demo.api_05_app
{
    public partial class VerifyAppData : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
                        
            /**
             * �Կؼ����̻�APP���ص�Ӧ����Ϣ��ǩ��ǰ����ֱ�Ӱ�string�͵�json��post����
             */
            StreamReader reader = new StreamReader(Request.InputStream);
            string data = reader.ReadToEnd();
            Response.Write(AcpService.ValidateAppResponse(data, Encoding.UTF8)?"true":"false");
        }

    }
}
