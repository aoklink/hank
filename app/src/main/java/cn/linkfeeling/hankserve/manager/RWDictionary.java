package cn.linkfeeling.hankserve.manager;

import android.provider.ContactsContract;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class RWDictionary<T> {
 
     private final Map<String, ContactsContract.Contacts.Data> m = new TreeMap<String, ContactsContract.Contacts.Data>();
 
     private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
 
     private final Lock r = rwl.readLock();
 
     private final Lock w = rwl.writeLock();
 
 
 
     public ContactsContract.Contacts.Data get(String key) {
 
         r.lock(); try { return m.get(key); } finally { r.unlock(); }
 
     }
 
     public Object[] allKeys() {
 
         r.lock(); try { return m.keySet().toArray(); } finally { r.unlock(); }
 
     }
 
     public ContactsContract.Contacts.Data put(String key, ContactsContract.Contacts.Data value) {
 
         w.lock(); try { return m.put(key, value); } finally { w.unlock(); }
 
     }
 
     public void clear() {
 
         w.lock(); try { m.clear(); } finally { w.unlock(); }
 
     }
 
  }