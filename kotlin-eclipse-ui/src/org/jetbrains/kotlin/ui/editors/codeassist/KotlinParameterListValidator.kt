/*******************************************************************************
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *******************************************************************************/
package org.jetbrains.kotlin.ui.editors.codeassist

import org.eclipse.jface.text.contentassist.IContextInformationPresenter
import org.eclipse.jface.text.contentassist.IContextInformationValidator
import org.eclipse.jface.text.contentassist.IContextInformation
import org.eclipse.jface.text.ITextViewer
import org.eclipse.jface.text.TextPresentation
import kotlin.properties.Delegates
import org.jetbrains.kotlin.ui.editors.KotlinFileEditor
import org.jetbrains.kotlin.psi.JetValueArgumentList
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.eclipse.ui.utils.EditorUtil
import org.jetbrains.kotlin.psi.JetValueArgument
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyleRange
import org.jetbrains.kotlin.eclipse.ui.utils.LineEndUtil
import org.jetbrains.kotlin.lexer.JetTokens
import com.intellij.psi.PsiErrorElement

public class KotlinParameterListValidator(val editor: KotlinFileEditor) : IContextInformationValidator, 
        IContextInformationPresenter {
    var info: IContextInformation by Delegates.notNull()
    var viewer: ITextViewer by Delegates.notNull()
    var position: Int by Delegates.notNull()
    var previousIndex: Int by Delegates.notNull()
    
    override fun install(info: IContextInformation, viewer: ITextViewer, offset: Int) {
        this.info = info
        this.viewer = viewer
        this.position = offset
        this.previousIndex = -1
    }
    
    override fun isContextInformationValid(offset: Int): Boolean {
        if (info !is KotlinFunctionParameterContextInformation) return false
        EditorUtil.updatePsiFile(editor)
        
        val document = viewer.getDocument()
        val line = document.getLineInformationOfOffset(position)
        
        if (offset < line.getOffset()) return false
        
        val expression = getCallSimpleNameExpression(editor, offset)
        
        return expression?.getReferencedName() == (info as KotlinFunctionParameterContextInformation).name.asString()
    }
    
    override fun updatePresentation(offset: Int, presentation: TextPresentation): Boolean {
        val currentArgumentIndex = getCurrentArgumentIndex(offset)
        if (currentArgumentIndex == null || previousIndex == currentArgumentIndex) {
            return false
        }
        presentation.clear()
        previousIndex = currentArgumentIndex
        
        val renderedParameter = (info as KotlinFunctionParameterContextInformation).renderedParameters[currentArgumentIndex]
        
        val displayString = info.getInformationDisplayString()
        val start = displayString.indexOf(renderedParameter)
        if (start >= 0) {
            presentation.addStyleRange(StyleRange(0, start, null, null, SWT.NORMAL))
            
            val end = start + renderedParameter.length()
            presentation.addStyleRange(StyleRange(start, end - start, null, null, SWT.BOLD))
            presentation.addStyleRange(StyleRange(end, displayString.length() - end, null, null, SWT.NORMAL))
            
            return true
        }
        
        return true
    }
    
//    Copied with some changes from JetFunctionParameterInfoHandler.java
    private fun getCurrentArgumentIndex(offset: Int): Int? {
        val psiElement = EditorUtil.getPsiElement(editor, offset)
        val argumentList = PsiTreeUtil.getNonStrictParentOfType(psiElement, javaClass<JetValueArgumentList>())
        if (argumentList == null) return null
        
        val offsetInPSI = LineEndUtil.convertCrToDocumentOffset(editor.document, offset)
        var child = argumentList.getNode().getFirstChildNode()
        var index = 0
        while (child != null && child.getStartOffset() < offsetInPSI) {
            if (child.getElementType() == JetTokens.COMMA || 
                (child.getText() == "," && child is PsiErrorElement)) ++index
            child = child.getTreeNext()
        }
        
        return index
    }
}