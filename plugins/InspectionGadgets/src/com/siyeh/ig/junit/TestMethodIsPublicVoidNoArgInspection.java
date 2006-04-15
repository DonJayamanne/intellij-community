/*
 * Copyright 2003-2006 Dave Griffith, Bas Leijdekkers
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
 */
package com.siyeh.ig.junit;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.*;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.MethodInspection;
import com.siyeh.ig.psiutils.ClassUtils;
import com.siyeh.InspectionGadgetsBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NonNls;

public class TestMethodIsPublicVoidNoArgInspection extends MethodInspection {

    public String getID() {
        return "TestMethodWithIncorrectSignature";
    }

    public String getGroupDisplayName() {
        return GroupNames.JUNIT_GROUP_NAME;
    }

    @NotNull
    public String buildErrorString(Object... infos) {
        return InspectionGadgetsBundle.message(
                "test.method.is.public.void.no.arg.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor() {
        return new TestMethodIsPublicVoidNoArgVisitor();
    }

    private static class TestMethodIsPublicVoidNoArgVisitor
            extends BaseInspectionVisitor {

        public void visitMethod(@NotNull PsiMethod method) {
            //note: no call to super;
            @NonNls final String methodName = method.getName();
            if (!methodName.startsWith("test") &&
                    !AnnotationUtil.isAnnotated(method, "org.junit.Test", true)) {
                return;
            }
            final PsiType returnType = method.getReturnType();
            if (returnType == null) {
                return;
            }
            final PsiParameterList parameterList = method.getParameterList();
            if (parameterList == null) {
                return;
            }
            final PsiParameter[] parameters = parameterList.getParameters();
            if (parameters == null) {
                return;
            }
            if (parameters.length == 0 && returnType.equals(PsiType.VOID) &&
                    method.hasModifierProperty(PsiModifier.PUBLIC)) {
                return;
            }
            final PsiClass targetClass = method.getContainingClass();
            if (!AnnotationUtil.isAnnotated(method, "org.junit.Test", true)) {
                if (targetClass == null || !ClassUtils.isSubclass(targetClass,
                                                                  "junit.framework.TestCase"))
                {
                    return;
                }
            }
            registerMethodError(method);
        }
    }
}
