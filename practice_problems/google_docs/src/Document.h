//
// Created by Aaditya on 2/9/26.
//

#ifndef LOW_LEVEL_DESIGN_DOCUMENT_H
#define LOW_LEVEL_DESIGN_DOCUMENT_H
#include "DocumentElement.h"
#include <vector>

class Document {
public:
    std::vector<DocumentElement *> documentElements;

    [[nodiscard]] std::vector<DocumentElement *> getElements() const;
    void addElement(DocumentElement *element);
    std::vector<string> render() const;
};


#endif //LOW_LEVEL_DESIGN_DOCUMENT_H