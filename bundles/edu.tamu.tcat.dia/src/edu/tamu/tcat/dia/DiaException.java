package edu.tamu.tcat.dia;

/**
 * Top-level exception for the document image analysis libraries. 
 */
public class DiaException extends Exception
{

   public DiaException()
   {
   }

   public DiaException(String message)
   {
      super(message);
   }

   public DiaException(Throwable cause)
   {
      super(cause);
   }

   public DiaException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public DiaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
   {
      super(message, cause, enableSuppression, writableStackTrace);
   }

}
