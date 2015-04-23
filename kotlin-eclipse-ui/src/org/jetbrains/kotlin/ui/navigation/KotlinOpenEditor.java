package org.jetbrains.kotlin.ui.navigation;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.core.builder.KotlinPsiManager;
import org.jetbrains.kotlin.core.log.KotlinLogger;
import org.jetbrains.kotlin.eclipse.ui.utils.EditorUtil;
import org.jetbrains.kotlin.psi.JetElement;
import org.jetbrains.kotlin.psi.JetFile;
import org.jetbrains.kotlin.ui.editors.KotlinEditor;

// Seeks for Kotlin editor by IJavaElement
public class KotlinOpenEditor {
	@Nullable
	public static IEditorPart openKotlinEditor(@NotNull BinaryType binaryType, boolean activate) {
		try {
			IBinaryType rawBinaryType = (IBinaryType) ((binaryType).getElementInfo());
			IPath sourceFilePath = new Path(binaryType.getSourceFileName(rawBinaryType));
	        IFile kotlinFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(sourceFilePath);
	        if (kotlinFile.exists()) {
	        	return EditorUtility.openInEditor(kotlinFile, activate);
	        }
		} catch (JavaModelException | PartInitException e) {
			KotlinLogger.logAndThrow(e);
		}
		
		return null;
	}
	
	public static void revealKotlinElement(@NotNull KotlinEditor kotlinEditor, @NotNull IJavaElement javaElement) {
	    IFile file = EditorUtil.getFile(kotlinEditor);
	    if (file != null) {
	        JetElement jetElement = findKotlinElement(javaElement, file);
	        if (jetElement != null) {
	            kotlinEditor.selectAndReveal(jetElement.getTextOffset(), 0);
	        }
	    }
	}

	@Nullable
	private static JetElement findKotlinElement(@NotNull final IJavaElement javaElement, @NotNull IFile file) {
	    JetFile jetFile = KotlinPsiManager.INSTANCE.getParsedFile(file);
	    List<JetElement> result = NavigationPackage.visitFile(javaElement, jetFile);
	    if (result.size() == 1) {
	        return result.get(0);
	    }
	    
	    return null;
	}
}
