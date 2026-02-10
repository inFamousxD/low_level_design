//
// Created by Aaditya on 2/9/26.
//

#ifndef LOW_LEVEL_DESIGN_DOCUMENTEDITOR_H
#define LOW_LEVEL_DESIGN_DOCUMENTEDITOR_H
#include "Document.h"


class DocumentEditor {
public:
    Document *document;

    void addText(const std::string& text) const;

    DocumentEditor(Document* document);
};


#endif //LOW_LEVEL_DESIGN_DOCUMENTEDITOR_H