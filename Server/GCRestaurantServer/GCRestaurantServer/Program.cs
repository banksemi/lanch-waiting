﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using NetworkLibrary;
using Newtonsoft.Json.Linq;
namespace GCRestaurantServer
{
    class Program
    {

        public static LogSystem.LogSystem LogSystem = new LogSystem.LogSystem();

        public static Dictionary<ESocket, OnlineUser> users = new Dictionary<ESocket, OnlineUser>();
        static void Main(string[] args)
        {
            Server server = new Server(1231);
            server.Connect += Server_Connect;
            server.Receive += Server_Receive_Try;
            server.Exit += Server_Exit;

            LogSystem.AddLog(3, "Program", "서버가 실행되었습니다.");
            while (true)
            {
                System.Threading.Thread.Sleep(4000);
                JObject json = new JObject();
                json["type"] = 1;
                lock(users)
                {
                    foreach(OnlineUser user in users.Values)
                    {
                        user.socket.Send(json);
                    }
                }
            }
        }

        private static void Server_Connect(ESocket socket)
        {
            LogSystem.AddLog(-1, "Program", "새로운 소켓이 연결되었습니다.");
            lock (users)
            {
                users.Add(socket, new OnlineUser(socket));
            }
        }
        private static void Server_Receive_Try(ESocket socket, JObject Message)
        {
            try
            {
                Server_Receive(socket, Message);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message + "\r\n" + e.StackTrace);
            }
        }
        private static void Server_Receive(ESocket socket, JObject Message)
        {
            LogSystem.AddLog(-1, "Program", Message.ToString());
            switch ((int)Message["type"])
            {
                case 1000:
                    LogSystem.AddLog(0, "Program", (string)Message["message"]);
                    break;
            }
        }



        private static void Server_Exit(ESocket socket)
        {
            LogSystem.AddLog(-1, "Program", "기존 소켓의 연결이 해제되었습니다.");
            lock (users)
            {
                users.Remove(socket);
            }
        }
    }
}
