package edu.tamu.tcat.dia.morphological;

public class MorphologicalOperationException extends Exception
{

   public MorphologicalOperationException()
   {
   }

   public MorphologicalOperationException(String message)
   {
      super(message);
   }

   public MorphologicalOperationException(Throwable cause)
   {
      super(cause);
   }

   public MorphologicalOperationException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public MorphologicalOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
   {
      super(message, cause, enableSuppression, writableStackTrace);
   }

}
