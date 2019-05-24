﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json.Linq;
using System.Net;
using System.IO;
using System.Web;
using HtmlAgilityPack;
namespace GCRestaurantServer
{
    public static class NaverAPIModule
    {
        public static JObject SearchPlace(string client_id, string client_key, string keyword)
        {
            HttpWebRequest hreq = (HttpWebRequest)WebRequest.Create("https://openapi.naver.com/v1/search/local?query="+ HttpUtility.HtmlEncode(keyword) + "&display=30&start=1&sort=random");
            hreq.Method = "GET";
            hreq.ContentType = "plain/text;charset=utf-8";
            hreq.Headers["X-Naver-Client-Id"] = client_id;
            hreq.Headers["X-Naver-Client-Secret"] = client_key;
            HttpWebResponse hres = (HttpWebResponse)hreq.GetResponse();
            if (hres.StatusCode == HttpStatusCode.OK)
            {
                Stream dataStream = hres.GetResponseStream();
                StreamReader sr = new StreamReader(dataStream, Encoding.UTF8);
                string result = sr.ReadToEnd();
                dataStream.Close();
                sr.Close();
                return JObject.Parse(result);
            }
            return null;
        }

        public static string GetPlaceDescription(int id)
        {
            JObject result = GetInfomationDetail(id);
            return (string)result["business"][id.ToString()]["base"]["description"];
        }

        public static JObject GetInfomationDetail(int id)
        {
            HtmlDocument dom = ParseSupport.Crawling("https://store.naver.com/restaurants/detail?id=" + HttpUtility.HtmlEncode(id));
            HtmlNode ds = dom.DocumentNode.SelectSingleNode("//body/script");

            string result = ParseSupport.UTF8Char((string)ds.InnerHtml);
            result = ds.InnerHtml.Substring(result.IndexOf("=") + 1);
            return JObject.Parse(result);
        }

        public static JArray GetPlaceMenu(int id)
        {
            JObject result = GetInfomationDetail(id);
            JArray menus =(JArray)result["business"][id.ToString()]["biz"]["menus"];
            return menus;
        }
        public static int GetPlaceID(string RestaurantTitle)
        {
            HtmlDocument dom = ParseSupport.Crawling("https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=" + HttpUtility.HtmlEncode(RestaurantTitle));
            HtmlNode ds = null;

            ds = dom.DocumentNode.SelectSingleNode("//a[@class='api_more_theme']");
            if (ds != null)
                return (int)ParseSupport.UrlQueryParser(ds)["id"];

            // 만약 상단에 플레이스 정보가 없다면, 지도 카테고리도 확인한다.

            ds = dom.DocumentNode.SelectSingleNode("//a[@title='" + RestaurantTitle + "']");
            if (ds != null)
                return (int)ParseSupport.UrlQueryParser(ds)["code"];

            return -1;
        }
        public static int GetPlaceID(string RestaurantTitle, string roadAddress)
        {
            HtmlDocument dom = ParseSupport.Crawling("https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=" + HttpUtility.HtmlEncode(roadAddress));
            HtmlNodeCollection ds = dom.DocumentNode.SelectNodes("//dd[@class='vicinity']/a");
            foreach (HtmlNode node in ds)
            {
                if (node.InnerText == RestaurantTitle)
                {
                    return (int)ParseSupport.UrlQueryParser(node)["code"];
                }
            }
            // 도로명으로 검색이 불가능 한 경우 이름으로 재검색
            return GetPlaceID(RestaurantTitle);
        }
    }
}