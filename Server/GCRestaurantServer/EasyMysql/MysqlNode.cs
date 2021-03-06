﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MySql.Data.MySqlClient;
namespace EasyMysql
{
    public class MysqlNode : IDisposable
    {
        private string sql;
        private MysqlOption option;
        private MySqlConnection conn = null;
        private Dictionary<String, object> Parameters = new Dictionary<string, object>();
        public MySqlDataReader Reader { get; private set; }
        #region
        public string GetString(string name)
        {

            if (Reader[name] is DBNull)
            {
                return null;
            }
            return Reader.GetString(name);
        }
        public bool IsNull(string name)
        {
            return Reader[name] is DBNull;
        }
        public int GetInt(string name)
        {
            return Reader.GetInt32(name);
        }
        public float GetFloat(string name)
        {
            return Reader.GetFloat(name);
        }
        public double GetDouble(string name)
        {
            return Reader.GetDouble(name);
        }
        public DateTime GetDateTime(string name)
        {
            return Reader.GetDateTime(name);
        }
        #endregion
        public MysqlNode(MysqlOption option, string sql)
        {
            this.option = option;
            ChangeSql(sql);
        }
        public void ChangeSql(string sql)
        {
            this.sql = sql;
        }

        public bool Read()
        {
            return Reader.Read();
        }
        public void AddParameter(string index, object data)
        {
            if (ContainParameter(index)) Parameters[index] = data;
            else
            {
                Parameters.Add(index, data);
            }
        }
        public bool ContainParameter(string index)
        {
            if (Parameters.ContainsKey(index)) return true;
            return false;
        }
        public object this[string index]    // Indexer declaration  
        {
            get
            {
                if (ContainParameter(index)) return Parameters[index];
                else return null;
            }
            set
            {
                AddParameter(index, value);
            }
        }
        public void AllClose()
        {
            if (Reader != null) Reader.Close();
            if (conn != null) conn.Close();
            conn = null;
            Reader = null;
        }
        private MySqlCommand Open()
        {
            AllClose(); // 모든 객체 닫기
            conn = new MySqlConnection(option.ToString());
            conn.Open();
            MySqlCommand cmd = new MySqlCommand(sql, conn);
            foreach (string key in Parameters.Keys)
            {
                cmd.Parameters.Add(new MySqlParameter(key, Parameters[key]));
            }
            return cmd;
        }
        public MysqlNode ExecuteReader()
        {
            MySqlCommand cmd = Open();
            Reader = cmd.ExecuteReader();
            return this;
        }
        public int ExecuteNonQuery()
        {
            MySqlCommand cmd = Open();
            int result = 0;
            try
            {
                result = cmd.ExecuteNonQuery();
            }
            catch (MySqlException e)
            {
                if (e.Number == 1062)
                    return -1;
                else
                    throw e;
            }
            AllClose();
            return result;
        }
        public long ExecuteInsertQuery()
        {
            MySqlCommand cmd = Open();
            int result = 0;
            try
            {
                result = cmd.ExecuteNonQuery();
            }
            catch (MySqlException e)
            {
                if (e.Number == 1062)
                    return -1;
                else
                    throw e;
            }
            AllClose();
            return cmd.LastInsertedId;
        }
        public void Dispose()
        {
            AllClose();
        }
    }
}
