	@RequestMapping("/upload")
	public @ResponseBody Map<String, String> upload(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, String> resultMap = new HashMap<String, String>();
		//��������
		FileItemFactory factory = new DiskFileItemFactory();
		/*
		 * ��������������
		 */
		ServletFileUpload sfu = new ServletFileUpload(factory);
		sfu.setFileSizeMax(1024 * 1024); //���õ����ϴ��ļ��Ĵ�С����
		/*
		 * ����request�õ�List<FileItem>
		 */
		List<FileItem> fileItemList = null;
		List<FileItem> photoItemList = new ArrayList<FileItem>();
		try {
			fileItemList = sfu.parseRequest(request);
		} catch (FileUploadException e) {
			logger.error("�����ϴ��ļ�����1M", e);
			resultMap.put("E0030", Const.E0030);
			return resultMap;
		}

		/*
		 * ��ȡ���е�ͼƬ
		 */
		Iterator<FileItem> iter = fileItemList.iterator();
		while (iter.hasNext()) {
			FileItem item = iter.next();
			if (!item.isFormField()) {
				photoItemList.add(item);
			}
		}

		/*
		 * ���û���ļ�
		 */
		if (null == photoItemList || photoItemList.isEmpty()) {
			logger.info("�����ڸ��ļ�");
			resultMap.put("E0033", Const.E0033);
			return resultMap;
		}
		// ��ȡ�ļ���
		FileItem fileItem = photoItemList.get(0);
		//�ļ���ʶ
		logger.info("file field name ---- " + fileItem.getFieldName());
		String filename = fileItem.getName();
		// ��ȡ�ļ�������Ϊ����������ϴ��ľ���·��
		int index = filename.lastIndexOf("\\");
		if (index != -1) {
			filename = filename.substring(index + 1);
		}
		// ���ļ������uuidǰ׺�������ļ�ͬ������
		String uuid = UUID.randomUUID().toString();
		filename = uuid + "_" + filename;
		// У���ļ����Ƶ���չ��
		if (!filename.toLowerCase().endsWith(".jpg")) {
			logger.info("�ϴ���ͼƬ��չ��������JPG");
			resultMap.put("E0033", Const.E0033);
			return resultMap;
		}
		/*
		 * ����ͼƬ��
		 * 1. ��ȡ��ʵ·��
		 */
		String savepath = uploadMap.get("upload_address");

		/*
		 * 2. ����Ŀ���ļ�
		 */
		File destFile = new File(savepath, filename);
		/*
		 * 3. �����ļ�
		 */
		try {
			fileItem.write(destFile);//�������ʱ�ļ��ض���ָ����·������ɾ����ʱ�ļ�
		} catch (Exception e) {
			logger.error("�ϴ��ļ�ʧ��", e);
			resultMap.put("E0031", Const.E0031);
			return resultMap;
		}

		resultMap.put("S0032", Const.S0032);
		return resultMap;
	}