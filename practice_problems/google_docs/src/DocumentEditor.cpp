//
// Created by Aaditya on 2/9/26.
//

#include "DocumentEditor.h"

#include "TextElement.h"

void DocumentEditor::addText(const std::string& text) const {
    document->addElement(new TextElement(text));
}

DocumentEditor::DocumentEditor(Document *document) {
    this->document = document;
}
