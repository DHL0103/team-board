import { Editor } from 'https://esm.sh/@tiptap/core@2';
import StarterKit from 'https://esm.sh/@tiptap/starter-kit@2';
import Image from 'https://esm.sh/@tiptap/extension-image@2';
import Underline from 'https://esm.sh/@tiptap/extension-underline@2';
import Link from 'https://esm.sh/@tiptap/extension-link@2';

class PostEditorPage {
    static #initialized = false;

    static init() {
        if (PostEditorPage.#initialized) { return; }
        PostEditorPage.#initialized = true;

        const editorEl = document.getElementById('post-editor');
        if (!editorEl) { return; }

        const initialContent = editorEl.dataset.content || '';
        PostEditorPage.#initEditor('post-editor', 'post-content', 'post-toolbar', initialContent);
    }

    static #initEditor(editorId, inputId, toolbarId, initialContent) {
        const editorEl = document.getElementById(editorId);
        const inputEl  = document.getElementById(inputId);
        const toolbar  = document.getElementById(toolbarId);
        if (!editorEl || !inputEl || !toolbar) { return; }

        const editor = new Editor({
            element: editorEl,
            extensions: [
                StarterKit,
                Image.configure({ inline: false, allowBase64: false }),
                Underline,
                Link.configure({
                    openOnClick: false,
                    HTMLAttributes: { target: '_blank', rel: 'noopener noreferrer' },
                }),
            ],
            content: initialContent || '',
        });

        PostEditorPage.#bindToolbar(editor, toolbar, inputEl, editorEl);
        PostEditorPage.#bindImageUpload(editor, toolbar);

        editor.on('selectionUpdate', () => PostEditorPage.#updateToolbar(editor, toolbar));
        editor.on('transaction',     () => {
            PostEditorPage.#updateToolbar(editor, toolbar);
            PostEditorPage.#updateImageCounter(editor, toolbar);
        });

        editorEl.closest('form').addEventListener('submit', () => {
            inputEl.value = editor.getHTML();
        });
    }

    static #bindToolbar(editor, toolbar, inputEl, editorEl) {
        toolbar.querySelector('[data-cmd="h1"]')
            .addEventListener('click', () => editor.chain().focus().toggleHeading({ level: 1 }).run());
        toolbar.querySelector('[data-cmd="h2"]')
            .addEventListener('click', () => editor.chain().focus().toggleHeading({ level: 2 }).run());
        toolbar.querySelector('[data-cmd="h3"]')
            .addEventListener('click', () => editor.chain().focus().toggleHeading({ level: 3 }).run());
        toolbar.querySelector('[data-cmd="link"]').addEventListener('click', () => {
            if (editor.isActive('link')) {
                editor.chain().focus().unsetLink().run();
                return;
            }
            const { from, to } = editor.state.selection;
            const selectedText = editor.state.doc.textBetween(from, to).trim();
            if (selectedText) {
                editor.chain().focus().setLink({ href: selectedText }).run();
            }
        });
        toolbar.querySelector('[data-cmd="underline"]')
            .addEventListener('click', () => editor.chain().focus().toggleUnderline().run());
        toolbar.querySelector('[data-cmd="bold"]')
            .addEventListener('click', () => editor.chain().focus().toggleBold().run());
        toolbar.querySelector('[data-cmd="italic"]')
            .addEventListener('click', () => editor.chain().focus().toggleItalic().run());
        toolbar.querySelector('[data-cmd="strike"]')
            .addEventListener('click', () => editor.chain().focus().toggleStrike().run());
        toolbar.querySelector('[data-cmd="bulletList"]')
            .addEventListener('click', () => editor.chain().focus().toggleBulletList().run());
        toolbar.querySelector('[data-cmd="orderedList"]')
            .addEventListener('click', () => editor.chain().focus().toggleOrderedList().run());
        toolbar.querySelector('[data-cmd="codeBlock"]')
            .addEventListener('click', () => editor.chain().focus().toggleCodeBlock().run());
    }

    static #bindImageUpload(editor, toolbar) {
        const imgInput = toolbar.querySelector('.editor-img-input');
        toolbar.querySelector('[data-cmd="image"]').addEventListener('click', () => imgInput.click());
        imgInput.addEventListener('change', () => {
            const file = imgInput.files[0];
            if (!file) { return; }

            if (file.size > 5 * 1024 * 1024) {
                Toast.show('인라인 이미지 크기는 5MB를 초과할 수 없습니다.');
                imgInput.value = '';
                return;
            }

            let imageCount = 0;
            editor.state.doc.descendants(node => { if (node.type.name === 'image') { imageCount++; } });
            if (imageCount >= 5) {
                Toast.show('이미지는 최대 5장까지 첨부할 수 있습니다.');
                imgInput.value = '';
                return;
            }

            const formData = new FormData();
            formData.append('file', file);
            fetch('/posts/image', { method: 'POST', body: formData })
                .then(res => res.json())
                .then(data => {
                    editor.chain().focus().setImage({ src: data.url }).run();
                    const endPos = editor.state.doc.content.size;
                    editor.chain().insertContentAt(endPos, { type: 'paragraph' }).run();
                });
            imgInput.value = '';
        });
    }

    static #updateImageCounter(editor, toolbar) {
        const imgCounter = toolbar.querySelector('.editor-img-counter');
        let count = 0;
        editor.state.doc.descendants(node => { if (node.type.name === 'image') { count++; } });
        if (count > 0) {
            imgCounter.textContent = count + ' / 5';
            imgCounter.style.display = '';
        } else {
            imgCounter.style.display = 'none';
        }
    }

    static #updateToolbar(editor, toolbar) {
        const states = ['link', 'underline', 'bold', 'italic', 'strike', 'bulletList', 'orderedList', 'codeBlock'];
        states.forEach(name => {
            const btn = toolbar.querySelector(`[data-cmd="${name}"]`);
            if (btn) {
                btn.classList.toggle('active', editor.isActive(name));
            }
        });
        [1, 2, 3].forEach(level => {
            const btn = toolbar.querySelector(`[data-cmd="h${level}"]`);
            if (btn) {
                btn.classList.toggle('active', editor.isActive('heading', { level }));
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', () => PostEditorPage.init());