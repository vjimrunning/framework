SELECT *
  FROM ANNOUNCEMENT T
 WHERE 1 = 1
   AND T.ANNOUNCEMENT_ID IN :ids
   AND T.STATE = :state