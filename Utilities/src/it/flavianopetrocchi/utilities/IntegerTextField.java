/*
 * IntegerTextField.java
 *
 * Copyright (c) 2010 Flaviano Petrocchi <flavianopetrocchi at gmail.com>.
 * All rights reserved.
 *
 * This file is part of JPdfBookmarks.
 *
 * JPdfBookmarks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPdfBookmarks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JPdfBookmarks.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.flavianopetrocchi.utilities;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.text.AttributeSet;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

 public class IntegerTextField extends JTextField {

     public IntegerTextField(int cols) {
         super(cols);
		 setMaximumSize(new Dimension(getColumnWidth() * cols, 24));
		 addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				setSelectionStart(0);
				setSelectionEnd(getText().length());
			}

			public void focusLost(FocusEvent e) {
			}
		});
     }

	 public int getInteger() {
		 int num = 0;
		 try {
			 num = Integer.parseInt(getText());
		 } catch (NumberFormatException exc) {
		 }
		 return num;
	 }

	 public void setInteger(int num) {
		 setText(String.valueOf(num));
	 }

	 @Override
     protected Document createDefaultModel() {
 	      return new NumberTextDocument();
     }

     static class NumberTextDocument extends PlainDocument {

		 @Override
         public void insertString(int offs, String str, AttributeSet a)
 	          throws BadLocationException {

 	          if (str == null) {
				return;
 	          }
 	          char[] origin = str.toCharArray();
			  StringBuffer numbersOnly = new StringBuffer("");
 	          for (int i = 0; i < origin.length; i++) {
				  if (Character.isDigit(origin[i])) {
					  numbersOnly.append(origin[i]);
				  }
 	          }
 	          super.insertString(offs, numbersOnly.toString(), a);
 	      }
     }
 }
