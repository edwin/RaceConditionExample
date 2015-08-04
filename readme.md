A Simple Test Case for Simulating a Race Condition 
===================

Tools
-------------------
* Programming Language - Java
* IDE - Netbeans 8.0.2
* DB - Oracle
* Build Tool - Maven

Output Example
--------------------
>Thread2 query nomer surat S-301PKP/WPJ.10/KP.1003/2015 for 6 ms 

>Thread1 query nomer surat S-302PKP/WPJ.10/KP.1003/2015 for 6 ms 

>Thread2 query nomer surat S-303PKP/WPJ.10/KP.1003/2015 for 4 ms 

>Thread1 query nomer surat S-303PKP/WPJ.10/KP.1003/2015 for 4 ms  -- > **Duplicate**

>Thread1 query nomer surat S-304PKP/WPJ.10/KP.1003/2015 for 4 ms 

>Thread2 query nomer surat S-304PKP/WPJ.10/KP.1003/2015 for 4 ms  -- > **Duplicate**

>Thread1 query nomer surat S-305PKP/WPJ.10/KP.1003/2015 for 4 ms 

>Thread2 query nomer surat S-305PKP/WPJ.10/KP.1003/2015 for 5 ms  -- > **Duplicate**


Query
--------------------
``` sql
SELECT COUNT(1) AS total,
  X_DEBUG.value
FROM X_DEBUG
GROUP BY X_DEBUG.value
HAVING COUNT(1) > 1
ORDER BY total;
```