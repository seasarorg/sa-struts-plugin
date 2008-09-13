package org.seasar.sastrutsplugin.ui.editor.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jst.jsp.ui.internal.contentassist.JSPContentAssistProcessor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.seasar.eclipse.common.util.LogUtil;
import org.seasar.eclipse.common.util.WorkbenchUtil;
import org.seasar.sastrutsplugin.Activator;
import org.seasar.sastrutsplugin.bean.FormInfomation;
import org.seasar.sastrutsplugin.util.HTMLUtil;
import org.seasar.sastrutsplugin.util.JavaUtil;
import org.seasar.sastrutsplugin.util.SAStrutsUtil;
import org.seasar.sastrutsplugin.util.StringUtil;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class JSPAssistProcessor extends JSPContentAssistProcessor {

	private static final List<String> strutsTagList = new ArrayList<String>();

	static {
		strutsTagList.add("html:text");
		strutsTagList.add("html:textarea");
		strutsTagList.add("html:checkbox");
		strutsTagList.add("html:multibox");
		strutsTagList.add("html:select");
		strutsTagList.add("html:radio");
		strutsTagList.add("html:hidden");
		strutsTagList.add("html:password");
		strutsTagList.add("html:file");
	}

	private static final List<String> strutsTagButtonList = new ArrayList<String>();

	static {
		strutsTagButtonList.add("html:button");
		strutsTagButtonList.add("html:submit");
		strutsTagButtonList.add("html:cancel");
	}

	private static final List<String> inputTagTypeList = new ArrayList<String>();

	static {
		inputTagTypeList.add("text");
		inputTagTypeList.add("checkbox");
		inputTagTypeList.add("select");
		inputTagTypeList.add("radio");
		inputTagTypeList.add("hidden");
		inputTagTypeList.add("password");
		inputTagTypeList.add("file");
	}

	private static final List<String> inputTagTypeButtonList = new ArrayList<String>();

	static {
		inputTagTypeButtonList.add("button");
		inputTagTypeButtonList.add("submit");
		inputTagTypeButtonList.add("cancel");
	}

	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest) {

		String tagName = contentAssistRequest.getNode().getNodeName();
		int beginPos = contentAssistRequest.getReplacementBeginPosition();
		NamedNodeMap attributeMap = contentAssistRequest.getNode()
				.getAttributes();

		String cursorPositiondAttributeName = null;
		String typeAttributeValue = null;
		for (int i = 0; i < attributeMap.getLength(); i++) {
			Node node = attributeMap.item(i);
			if (node instanceof AttrImpl) {
				AttrImpl attrImpl = (AttrImpl) node;
				String attrubuteName = attrImpl.getName();
				if (attrubuteName.equals("type")) {
					typeAttributeValue = attrImpl.getValue();
				}
				int start = attrImpl.getStartOffset();
				int end = attrImpl.getEndOffset();
				if (beginPos > start && beginPos <= end) {
					cursorPositiondAttributeName = attrImpl.getName();
					break;
				}
			}
		}

		if (isContentAssistField(tagName, typeAttributeValue,
				cursorPositiondAttributeName)) {
			IType actionType = getActionType();
			if (actionType != null) {
				IType actionForm = null;
				try {
					for (IField field : actionType.getFields()) {
						int flag = field.getFlags();
						if ((Flags.isPublic(flag) || Flags.isProtected(flag))
								&& !Flags.isStatic(flag)) {
							if (isActionForm(field)) {
								String clazz = JavaUtil.getFullQName(
										actionType, Signature.toString(field
												.getTypeSignature()));
								actionForm = field.getJavaProject().findType(
										clazz);
							}
						}
					}
					if (actionForm != null) {
						ArrayList<IField> fields = new ArrayList<IField>();
						for (IField field : actionForm.getFields()) {
							int flag = field.getFlags();
							if (Flags.isPublic(flag) && !Flags.isStatic(flag)) {
								fields.add(field);
							}
						}
						for (IField field : fields) {
							String value = encloseDoubleQuotes(field
									.getElementName());

							contentAssistRequest
									.addProposal(getCompletionProposal(
											field,
											contentAssistRequest
													.getReplacementBeginPosition(),
											contentAssistRequest
													.getReplacementLength()));

						}
					}
				} catch (JavaModelException e) {
					LogUtil.log(Activator.getDefault(), e);
				}
			}
		}

		if (isContentAssistMethod(tagName, typeAttributeValue,
				cursorPositiondAttributeName)) {
			IType actionType = getActionType();
			if (actionType != null) {
				ArrayList<IMethod> executeMethods = new ArrayList<IMethod>();
				try {
					for (IMethod method : actionType.getMethods()) {
						if (isExecuteMethod(method)) {
							executeMethods.add(method);
						}
					}
					for (IMethod executeMethod : executeMethods) {
						contentAssistRequest.addProposal(getCompletionProposal(
								executeMethod, contentAssistRequest
										.getReplacementBeginPosition(),
								contentAssistRequest.getReplacementLength()));
					}
				} catch (JavaModelException e) {
					LogUtil.log(Activator.getDefault(), e);
				}
			}
		}
		super.addAttributeNameProposals(contentAssistRequest);
	}

	private IType getActionType() {
		FormInfomation formInfomation = SAStrutsUtil.getFormInformation();
		IEditorPart editor = WorkbenchUtil.getActiveEditor();
		IEditorInput input = editor.getEditorInput();
		IFile jspFile = ((IFileEditorInput) input).getFile();
		IType actionType = SAStrutsUtil.getActionType(jspFile,
				formInfomation.actionAttribute);
		return actionType;
	}

	private boolean isExecuteMethod(IMethod method) {
		try {
			int flag = method.getFlags();
			int numberOfParameters = method.getNumberOfParameters();
			if (Flags.isPublic(flag) && numberOfParameters == 0
					&& hasExecuteAnnotation(method)) {
				return true;
			}
		} catch (JavaModelException e) {
			LogUtil.log(Activator.getDefault(), e);
		}
		return false;
	}

	private boolean hasExecuteAnnotation(IMethod method) {
		try {
			return method.getSource() == null ? false : method.getSource()
					.indexOf("@Execute") > -1;
		} catch (JavaModelException e) {
			LogUtil.log(Activator.getDefault(), e);
			return false;
		}
	}

	private CompletionProposal getCompletionProposal(IMethod method,
			int replacementBeginPosition, int replacementLength) {
		String executeMethodName = encloseDoubleQuotes(method.getElementName());
		return new CompletionProposal(executeMethodName,
				replacementBeginPosition, replacementLength, executeMethodName
						.length(), JavaUI.getSharedImages().getImage(
						ISharedImages.IMG_OBJS_PUBLIC), method.getElementName()
						+ " - " + method.getDeclaringType().getElementName(),
				null, HTMLUtil.extractJavadoc(method));
	}

	private CompletionProposal getCompletionProposal(IField field,
			int replacementBeginPosition, int replacementLength) {
		String fieldName = encloseDoubleQuotes(field.getElementName());
		return new CompletionProposal(fieldName, replacementBeginPosition,
				replacementLength, fieldName.length(), JavaUI.getSharedImages()
						.getImage(ISharedImages.IMG_FIELD_PUBLIC), field
						.getElementName()
						+ " - " + field.getDeclaringType().getElementName(),
				null, HTMLUtil.extractJavadoc(field));
	}

	private boolean isActionForm(IField field) {
		if (field.getElementName().endsWith("Form")
				&& hasActionFormAnnotation(field)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean hasActionFormAnnotation(IField field) {
		try {
			return field.getSource() == null ? false : field.getSource()
					.indexOf("@ActionForm") > -1;
		} catch (JavaModelException e) {
			LogUtil.log(Activator.getDefault(), e);
			return false;
		}
	}

	private String encloseDoubleQuotes(String str) {
		return "\"" + str + "\"";
	}

	private boolean isContentAssistField(String tagName,
			String typeAttributeValue, String cursorPositiondAttributeName) {
		if (strutsTagList.contains(tagName)) {
			if (!StringUtil.isEmpty(cursorPositiondAttributeName)
					&& cursorPositiondAttributeName.equals("property")) {
				return true;
			}
		} else if (tagName.equals("input")) {
			if (inputTagTypeList.contains(typeAttributeValue)) {
				return true;
			}
		} else if (tagName.equals("textarea")) {
			if (!StringUtil.isEmpty(cursorPositiondAttributeName)
					&& cursorPositiondAttributeName.equals("name")) {
				return true;
			}
		}
		return false;
	}

	private boolean isContentAssistMethod(String tagName,
			String typeAttributeValue, String cursorPositiondAttributeName) {
		if (strutsTagButtonList.contains(tagName)) {
			if (!StringUtil.isEmpty(cursorPositiondAttributeName)
					&& cursorPositiondAttributeName.equals("property")) {
				return true;
			}
		} else if (tagName.equals("input")) {
			if (inputTagTypeButtonList.contains(typeAttributeValue)) {
				if (!StringUtil.isEmpty(cursorPositiondAttributeName)
						&& cursorPositiondAttributeName.equals("name")) {
					return true;
				}
			}
		}
		return false;
	}

}
