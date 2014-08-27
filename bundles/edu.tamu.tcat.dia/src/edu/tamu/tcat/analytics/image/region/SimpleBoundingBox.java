/* File: SimpleBoundingBox.java
 * Created: Nov 3, 2012
 * Author: Neal Audenaert
 *
 * Copyright 2012 Digital Archives, Research & Technology Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.tamu.tcat.analytics.image.region;

/**
 * Defines a bounding box on a page. 
 *  
 * @author Neal Audenaert
 */
public final class SimpleBoundingBox implements BoundingBox {
    public final int left;
    public final int top;
    public final int right;
    public final int bottom;
    
    public SimpleBoundingBox(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        
        if ((top > bottom) || (left > right))
            throw new IllegalArgumentException("Invalid " + toString());
    }
    
    @Override
    public int getLeft() {
        return left;
    }
    
    @Override
    public int getTop() {
        return top;
    }
    
    @Override
    public int getRight() {
        return right;
    }
    
    @Override
    public int getBottom() {
        return bottom;
    }
    
    @Override
    public int getWidth() {
        return right - left;
    }
    
    @Override
    public int getHeight() {
        return bottom - top;
    }
    
    @Override
    public int getArea() {
        return getWidth() * getHeight();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bounding Box: (")
          .append(left).append(", ").append(top).append(") x (")
          .append(right).append(", ").append(bottom).append(")");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleBoundingBox)
        {
            SimpleBoundingBox b = (SimpleBoundingBox)obj;
            return bottom == b.bottom && top == b.top && left == b.left && right == b.right;
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        
        result = result * 37 + top;
        result = result * 37 + bottom;
        result = result * 37 + left;
        result = result * 37 + right;
        
        return result;
    }
}