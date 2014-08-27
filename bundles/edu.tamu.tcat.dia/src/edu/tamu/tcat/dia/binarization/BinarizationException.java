package edu.tamu.tcat.dia.binarization;

import edu.tamu.tcat.dia.DiaException;

public class BinarizationException extends DiaException
{
   public BinarizationException()
   {
   }

   public BinarizationException(String message)
   {
      super(message);
   }

   public BinarizationException(Throwable cause)
   {
      super(cause);
   }

   public BinarizationException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public BinarizationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
   {
      super(message, cause, enableSuppression, writableStackTrace);
   }
}
