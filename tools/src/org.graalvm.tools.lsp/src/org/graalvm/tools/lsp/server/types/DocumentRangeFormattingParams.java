/*
 * Copyright (c) 2019, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.graalvm.tools.lsp.server.types;

import com.oracle.truffle.tools.utils.json.JSONObject;
import java.util.Objects;

/**
 * The parameters of a [DocumentRangeFormattingRequest](#DocumentRangeFormattingRequest).
 */
public class DocumentRangeFormattingParams extends WorkDoneProgressParams {

    DocumentRangeFormattingParams(JSONObject jsonData) {
        super(jsonData);
    }

    /**
     * The document to format.
     */
    public TextDocumentIdentifier getTextDocument() {
        return new TextDocumentIdentifier(jsonData.getJSONObject("textDocument"));
    }

    public DocumentRangeFormattingParams setTextDocument(TextDocumentIdentifier textDocument) {
        jsonData.put("textDocument", textDocument.jsonData);
        return this;
    }

    /**
     * The range to format.
     */
    public Range getRange() {
        return new Range(jsonData.getJSONObject("range"));
    }

    public DocumentRangeFormattingParams setRange(Range range) {
        jsonData.put("range", range.jsonData);
        return this;
    }

    /**
     * The format options.
     */
    public FormattingOptions getOptions() {
        return new FormattingOptions(jsonData.getJSONObject("options"));
    }

    public DocumentRangeFormattingParams setOptions(FormattingOptions options) {
        jsonData.put("options", options.jsonData);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        DocumentRangeFormattingParams other = (DocumentRangeFormattingParams) obj;
        if (!Objects.equals(this.getTextDocument(), other.getTextDocument())) {
            return false;
        }
        if (!Objects.equals(this.getRange(), other.getRange())) {
            return false;
        }
        if (!Objects.equals(this.getOptions(), other.getOptions())) {
            return false;
        }
        if (!Objects.equals(this.getWorkDoneToken(), other.getWorkDoneToken())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.getTextDocument());
        hash = 73 * hash + Objects.hashCode(this.getRange());
        hash = 73 * hash + Objects.hashCode(this.getOptions());
        if (this.getWorkDoneToken() != null) {
            hash = 73 * hash + Objects.hashCode(this.getWorkDoneToken());
        }
        return hash;
    }

    public static DocumentRangeFormattingParams create(TextDocumentIdentifier textDocument, Range range, FormattingOptions options) {
        final JSONObject json = new JSONObject();
        json.put("textDocument", textDocument.jsonData);
        json.put("range", range.jsonData);
        json.put("options", options.jsonData);
        return new DocumentRangeFormattingParams(json);
    }
}
